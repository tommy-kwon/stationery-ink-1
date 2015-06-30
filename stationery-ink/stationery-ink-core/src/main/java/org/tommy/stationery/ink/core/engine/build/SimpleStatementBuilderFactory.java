package org.tommy.stationery.ink.core.engine.build;

import net.hydromatic.linq4j.Linq4j;
import org.tommy.stationery.ink.core.config.ConfigProperties;
import org.tommy.stationery.ink.core.engine.spark.build.SparkStatementBuilder;
import org.tommy.stationery.ink.core.engine.storm.build.StormStatementsBuilder;
import org.tommy.stationery.ink.core.util.LinqQuery;
import org.tommy.stationery.ink.domain.BaseStatement;
import org.tommy.stationery.ink.enums.EngineTypeEnum;
import org.tommy.stationery.ink.enums.MessageEnum;
import org.tommy.stationery.ink.enums.SettingEnum;
import org.tommy.stationery.ink.exception.InkException;

import java.util.List;

/**
 * Created by kun7788 on 15. 6. 29..
 */
public class SimpleStatementBuilderFactory {

    public static IStatementBuilder getInstance(ConfigProperties configProperties, List<BaseStatement> statements) throws InkException {
        List<BaseStatement> settingStatements = Linq4j.asEnumerable(statements).where(LinqQuery.SETTING_STATEMENT_GROUP_FILTER).toList();
        EngineTypeEnum engineTypeEnum = null;
        for (BaseStatement settingStatement : settingStatements) {
            if (SettingEnum.ENGINE.equals(settingStatement.getSettingDef())) {
                engineTypeEnum = EngineTypeEnum.valueOf(settingStatement.getSettingDef().getValue());
                break;
            }
        }

        if (engineTypeEnum == null) {
            engineTypeEnum = EngineTypeEnum.valueOf(configProperties.getDefaultInkConfig().getString(SettingEnum.ENGINE));
        }

        if (EngineTypeEnum.STORM.equals(engineTypeEnum)) {
            return new StormStatementsBuilder();
        } else if (EngineTypeEnum.STORM.equals(engineTypeEnum)) {
            return new SparkStatementBuilder();
        } else {
            throw new InkException(MessageEnum.NO_ENGINE_SUPPORT);
        }
    }
}
