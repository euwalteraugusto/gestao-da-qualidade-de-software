# Consolida as saídas do PMD e do Checkstyle (pasta evidencias/) em evidencias/metricas-antes-depois.md.
# Uso (na raiz do projeto, depois de gerar os XML conforme o README):  powershell -File scripts/resumo-metricas.ps1
$ErrorActionPreference = 'Stop'
$raiz = Split-Path -Parent $PSScriptRoot
$ev = Join-Path $raiz 'evidencias'
$minPorViolacao = 5      # premissa: 5 min de correção por violação (aproximação do "debt" do Sonar)
$custoPorLinha = 30      # premissa: 30 min de desenvolvimento por linha (padrão do SonarQube)

function Ler-Violacoes($arquivo) {
    [xml]$x = Get-Content -Raw -Encoding UTF8 $arquivo
    foreach ($f in @($x.pmd.file)) {
        foreach ($v in @($f.violation)) {
            if ($v) { [pscustomobject]@{ Arquivo = (Split-Path -Leaf $f.name); Regra = $v.rule; Metodo = $v.method; Msg = $v.'#text'.Trim() } }
        }
    }
}

function Numero($texto, $padrao) {
    if ($texto -match $padrao) { [int]$Matches[1] } else { 0 }
}

function Contar-Linhas($pasta) {
    $total = 0
    foreach ($f in Get-ChildItem $pasta -Filter *.java -Recurse) {
        $total += (Get-Content $f.FullName -Encoding UTF8 | Where-Object {
            $t = $_.Trim(); $t -ne '' -and -not ($t.StartsWith('//') -or $t.StartsWith('/*') -or $t.StartsWith('*') -or $t.StartsWith('package ') -or $t.StartsWith('import '))
        }).Count
    }
    $total
}

$saida = @()
$saida += '# Métricas antes x depois (geradas por scripts/resumo-metricas.ps1)'
$saida += ''
$linhas = @{}
$debito = @{}
$resumo = @{}
foreach ($v in 'antes', 'depois') {
    $met = Ler-Violacoes (Join-Path $ev "pmd-metricas-$v.xml")
    $qual = @(Ler-Violacoes (Join-Path $ev "pmd-qualidade-$v.xml"))
    [xml]$cs = Get-Content -Raw -Encoding UTF8 (Join-Path $ev "checkstyle-$v.xml")
    $erros = @($cs.checkstyle.file.error | Where-Object { $_ })
    $csCount = $erros.Count
    $metodos = @($met | Where-Object { $_.Metodo })
    $ciclo = @($metodos | Where-Object { $_.Regra -eq 'CyclomaticComplexity' } | ForEach-Object { Numero $_.Msg 'cyclomatic complexity of (\d+)' })
    $cogn = @($metodos | Where-Object { $_.Regra -eq 'CognitiveComplexity' } | ForEach-Object { Numero $_.Msg 'cognitive complexity of (\d+)' })
    $npath = @($metodos | Where-Object { $_.Regra -eq 'NPathComplexity' } | ForEach-Object { Numero $_.Msg 'NPath complexity of (\d+)' })
    $ncss = @($metodos | Where-Object { $_.Regra -eq 'NcssCount' } | ForEach-Object { Numero $_.Msg 'NCSS line count of (\d+)' })
    $cicloClasse = @($met | Where-Object { -not $_.Metodo -and $_.Regra -eq 'CyclomaticComplexity' } | ForEach-Object { Numero $_.Msg 'total cyclomatic complexity of (\d+)' })
    $ncloc = Contar-Linhas (Join-Path $raiz "src/main/java/br/gqs/aula11/$v")
    $deb = $qual.Count * $minPorViolacao
    $ratio = if ($ncloc -gt 0) { 100.0 * $deb / ($ncloc * $custoPorLinha) } else { 0 }
    $nota = if ($ratio -le 5) { 'A' } elseif ($ratio -le 10) { 'B' } elseif ($ratio -le 20) { 'C' } elseif ($ratio -le 50) { 'D' } else { 'E' }
    $resumo[$v] = [pscustomobject]@{
        Violacoes = $qual.Count; Checkstyle = $csCount; Ncloc = $ncloc; Debito = $deb; Ratio = [math]::Round($ratio, 1); Nota = $nota
        CicloMax = ($ciclo | Measure-Object -Maximum).Maximum; CicloSoma = ($cicloClasse | Measure-Object -Sum).Sum
        CognMax = ($cogn | Measure-Object -Maximum).Maximum; NPathMax = ($npath | Measure-Object -Maximum).Maximum
        NcssMax = ($ncss | Measure-Object -Maximum).Maximum; Metodos = $metodos.Count
    }
    $saida += "## $v"
    $saida += ''
    $saida += '### Violações PMD (config/pmd-qualidade.xml) por regra'
    $saida += ''
    $saida += '| Regra | Quantidade |'
    $saida += '|-------|-----------:|'
    foreach ($g in ($qual | Group-Object Regra | Sort-Object Count -Descending)) { $saida += "| $($g.Name) | $($g.Count) |" }
    if ($qual.Count -eq 0) { $saida += '| (nenhuma) | 0 |' }
    $saida += ''
    $saida += '### Violações Checkstyle (config/checkstyle-qualidade.xml) por regra'
    $saida += ''
    $saida += '| Regra | Quantidade |'
    $saida += '|-------|-----------:|'
    $regras = @($erros | ForEach-Object { ($_.source -split '\.')[-1] -replace 'Check$', '' })
    foreach ($g in ($regras | Group-Object | Sort-Object Count -Descending)) { $saida += "| $($g.Name) | $($g.Count) |" }
    if ($regras.Count -eq 0) { $saida += '| (nenhuma) | 0 |' }
    $saida += ''
}
$a = $resumo['antes']; $d = $resumo['depois']
function Reducao($x, $y) { if ($x -eq 0) { 'n/a' } else { '{0:N0}%' -f (100.0 * ($x - $y) / $x) } }
$saida += '## Comparativo'
$saida += ''
$saida += '| Métrica | Antes | Depois | Variação |'
$saida += '|---------|------:|-------:|---------:|'
$saida += "| Violações PMD (smells) | $($a.Violacoes) | $($d.Violacoes) | -$(Reducao $a.Violacoes $d.Violacoes) |"
$saida += "| Violações Checkstyle | $($a.Checkstyle) | $($d.Checkstyle) | -$(Reducao $a.Checkstyle $d.Checkstyle) |"
$saida += "| Maior complexidade ciclomática de método | $($a.CicloMax) | $($d.CicloMax) | -$(Reducao $a.CicloMax $d.CicloMax) |"
$saida += "| Maior complexidade cognitiva de método | $($a.CognMax) | $($d.CognMax) | -$(Reducao $a.CognMax $d.CognMax) |"
$saida += "| Maior NPath de método | $($a.NPathMax) | $($d.NPathMax) | -$(Reducao $a.NPathMax $d.NPathMax) |"
$saida += "| Maior NCSS de método | $($a.NcssMax) | $($d.NcssMax) | -$(Reducao $a.NcssMax $d.NcssMax) |"
$saida += "| Linhas de código (ncloc, aprox.) | $($a.Ncloc) | $($d.Ncloc) | +$($d.Ncloc - $a.Ncloc) |"
$saida += "| Dívida estimada (min) = violações PMD x $minPorViolacao | $($a.Debito) | $($d.Debito) | -$(Reducao $a.Debito $d.Debito) |"
$saida += "| Dívida / (ncloc x $custoPorLinha min) | $($a.Ratio)% | $($d.Ratio)% | |"
$saida += "| Nota de manutenibilidade estimada (A<=5%, B<=10%, C<=20%, D<=50%, E) | $($a.Nota) | $($d.Nota) | |"
$saida += ''
$saida += "Premissas: $minPorViolacao min por violação e $custoPorLinha min por linha são aproximações do modelo do SonarQube; o valor oficial só sai do servidor Sonar."
$saida | Set-Content -Encoding UTF8 (Join-Path $ev 'metricas-antes-depois.md')
$saida
