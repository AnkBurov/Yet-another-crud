package io.github.sqlexecutor

import groovy.sql.Sql
import org.apache.commons.collections4.CollectionUtils

class SqlExecutorMain {
    static void main(String[] args) {
        if (args.size() != 5) {
            throw new RuntimeException('Wrong number of arguments. ' +
                    'Expected: String url, String user, String password, String driverName, String scriptsPath')
        }
        def dbHolder = [url: args[0], user: args[1], password: args[2], driverName: args[3]]
        new SqlExecutorMain().run(dbHolder, args[4])
    }

    private void run(Map<String, String> dhHolder, String scriptsPath) {
        def sql = Sql.newInstance(dhHolder.url, dhHolder.user, dhHolder.password, dhHolder.driverName)
        def appliedSqlScriptIds = getAppliedSqlScriptIds(sql, scriptsPath)
        def sqlScripts = getSqlScripts(scriptsPath)
        def sqlScriptsNeededToBeExecuted = CollectionUtils.removeAll(sqlScripts.keySet(), appliedSqlScriptIds).sort()
        for (String sqlScriptNeededToBeExecuted : sqlScriptsNeededToBeExecuted) {
            SqlUtils.executeSqlScript(sqlScripts.get(sqlScriptNeededToBeExecuted), sql)
        }
    }

    private List<String> getAppliedSqlScriptIds(Sql sql, String scriptsPath) {
        List<String> result = new ArrayList<>()
        try {
            sql.execute('select 1 from DBUPDATE')
        } catch (Exception e) {
            SqlUtils.createDbUpdateTable(sql, scriptsPath)
        }
        sql.eachRow('select SCRIPTID from DBUPDATE', { row ->
            result.add(row.scriptId.toString())
        })
        result
    }

    private Map<String, File> getSqlScripts(String scriptsPath) {
        Map<String, File> result = new LinkedHashMap<>()
        new File(scriptsPath).eachFileRecurse { file ->
            if (file.name.endsWith(".sql")) {
                result.put(file.name.replaceAll(".sql", ""), file)
            }
        }
        result
    }
}
