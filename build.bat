SET passphrase=%1
mvn -P release Dgpg.passphrase=%passphrase% -DignoreSnapshots=true release:clean release:prepare
mvn -P release Dgpg.passphrase=%passphrase% release:perform