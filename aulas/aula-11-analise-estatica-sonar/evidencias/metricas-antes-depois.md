# Métricas antes x depois (geradas por scripts/resumo-metricas.ps1)

## antes

### Violações PMD (config/pmd-qualidade.xml) por regra

| Regra | Quantidade |
|-------|-----------:|
| LiteralsFirstInComparisons | 11 |
| UseStringBufferForStringAppends | 6 |
| AvoidDuplicateLiterals | 5 |
| UnusedLocalVariable | 2 |
| CyclomaticComplexity | 2 |
| CognitiveComplexity | 2 |
| DataClass | 1 |
| AvoidDeeplyNestedIfStmts | 1 |
| ForLoopCanBeForeach | 1 |
| ControlStatementBraces | 1 |
| UnusedPrivateField | 1 |
| SystemPrintln | 1 |

### Violações Checkstyle (config/checkstyle-qualidade.xml) por regra

| Regra | Quantidade |
|-------|-----------:|
| MultipleStringLiterals | 6 |
| MethodLength | 2 |
| JavaNCSS | 2 |
| CyclomaticComplexity | 2 |
| NestedIfDepth | 1 |
| NPathComplexity | 1 |
| VisibilityModifier | 1 |
| NeedBraces | 1 |

## depois

### Violações PMD (config/pmd-qualidade.xml) por regra

| Regra | Quantidade |
|-------|-----------:|
| (nenhuma) | 0 |

### Violações Checkstyle (config/checkstyle-qualidade.xml) por regra

| Regra | Quantidade |
|-------|-----------:|
| (nenhuma) | 0 |

## Comparativo

| Métrica | Antes | Depois | Variação |
|---------|------:|-------:|---------:|
| Violações PMD (smells) | 34 | 0 | -100% |
| Violações Checkstyle | 16 | 0 | -100% |
| Maior complexidade ciclomática de método | 10 | 5 | -50% |
| Maior complexidade cognitiva de método | 16 | 3 | -81% |
| Maior NPath de método | 28 | 6 | -79% |
| Maior NCSS de método | 31 | 7 | -77% |
| Linhas de código (ncloc, aprox.) | 83 | 131 | +48 |
| Dívida estimada (min) = violações PMD x 5 | 170 | 0 | -100% |
| Dívida / (ncloc x 30 min) | 6.8% | 0% | |
| Nota de manutenibilidade estimada (A<=5%, B<=10%, C<=20%, D<=50%, E) | B | A | |

Premissas: 5 min por violação e 30 min por linha são aproximações do modelo do SonarQube; o valor oficial só sai do servidor Sonar.
