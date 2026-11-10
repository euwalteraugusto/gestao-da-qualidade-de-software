# Gera os relatorios Checkstyle/PMD "antes" e "depois" (rodar a partir da pasta da aula 3).
# Requer JAVA_HOME e Maven no PATH.
$ev = 'evidencias'
mvn -B clean verify '-Dquality.fail=true' 2>&1 | Out-File -Encoding utf8 "$ev\depois-mvn-clean-verify.txt"
mvn -B checkstyle:checkstyle pmd:pmd '-Dquality.fail=false' 2>&1 | Out-File -Encoding utf8 "$ev\depois-relatorios-mvn.txt"
Copy-Item target\checkstyle-result.xml "$ev\depois-checkstyle-result.xml"
Copy-Item target\pmd.xml "$ev\depois-pmd.xml"
mvn -B verify -DskipTests '-Dquality.includes=**/antes/**' '-Dquality.fail=false' 2>&1 | Out-File -Encoding utf8 "$ev\antes-mvn-verify.txt"
mvn -B checkstyle:checkstyle pmd:pmd '-Dquality.includes=**/antes/**' '-Dquality.fail=false' 2>&1 | Out-File -Encoding utf8 "$ev\antes-relatorios-mvn.txt"
Copy-Item target\checkstyle-result.xml "$ev\antes-checkstyle-result.xml"
Copy-Item target\pmd.xml "$ev\antes-pmd.xml"
