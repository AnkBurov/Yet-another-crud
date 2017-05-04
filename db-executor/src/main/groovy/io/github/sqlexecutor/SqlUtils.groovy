package io.github.sqlexecutor

import groovy.sql.Sql
import org.apache.commons.lang3.StringUtils

class SqlUtils {

    static void createDbUpdateTable(Sql sql, String scriptsPath) {
        try {
            executeSqlScript(new File(scriptsPath + '/1.sql'), sql)
        } catch (Exception innerException) {
            throw new RuntimeException('Cannot create initial DBUPDATE table', innerException)
        }
    }

    static void executeSqlScript(File script, Sql sql) {
        List<String> sqlQueries = loadSqlQueriesFromFile(script)
        for (String sqlQuery : sqlQueries) {
            try {
                executeSql(sqlQuery, sql)
            } catch (Exception e) {
                /*exception happened during SQL execution. Rollback is needed*/
                List<String> rollbackQueries = loadSqlQueriesFromFile(new File(script.path + ".rollback"))
                for (String rollbackQuery : rollbackQueries) {
                    executeSql(rollbackQuery, sql)
                }
            }
        }
    }

    private static List<String> loadSqlQueriesFromFile(File script) {
        String[] scriptContent = script.getText().split(';')
        List<String> sqlQueries = []
        for (String line : scriptContent) {
            if (StringUtils.isNotBlank(line)) {
                String newLine = line.replaceAll("[\r\n]", " ")
                if (StringUtils.isNotBlank(newLine)) {
                    sqlQueries.add(newLine)
                }
            }
        }
        sqlQueries
    }

    private static void executeSql(String sqlQuery, Sql sql) {
        sql.execute(sqlQuery)
    }
}
