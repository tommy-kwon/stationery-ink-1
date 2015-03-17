package org.tommy.stationery.ink.enums;

/**
 * Created by kun7788 on 15. 1. 27..
 */
public enum MessageEnum {
    SHELL_EXECUTE_ERROR("SHELL_EXECUTE_ERROR"),
    STORM_DEPLOY_ERROR("STORM_DEPLOY_ERROR"),
    STORM_KILL_ERROR("STORM_KILL_ERROR"),
    SETTING_IS_INVALID("SETTING_IS_INVALID"),
    SETTING_NAME_IS_NULL("SETTING_NAME_IS_NULL"),
    SETTING_VALUE_IS_NULL("SETTING_VALUE_IS_NULL"),
    SETTING_IS_NULL("SETTING_IS_NULL"),
    QUERY_RESULT_IS_NULL("QUERY_RESULT_IS_NULL"),
    LOOKUP_STATEMENT_MUST_HAS_ONLY_ONE_STREAM("LOOKUP_STATEMENT_MUST_HAS_ONLY_ONE_STREAM"),
    NOT_EXIST_COLUMNS("NOT_EXIST_COLUMNS"),
    DONT_USE_ASTERISKS("DONT_USE_ASTERISKS"),
    DONT_CREATE_BOLT("DONT_CREATE_BOLT"),
    JOB_NOT_FOUND("JOB_NOT_FOUND"),
    CATALOG_INVALID("CATALOG_INVALID"),
    ALREADY_EXIST_STREAM("ALREADY_EXIST_STREAM"),
    ALREADY_EXIST_SOURCE("ALREADY_EXIST_SOURCE"),
    ALREADY_EXIST_JOB("ALREADY_EXIST_JOB"),
    META_VALIDATION_ERROR("META_VALIDATION_ERROR"),
    SOURCE_NOT_FOUND("SOURCE_NOT_FOUND"),
    STREAM_NOT_FOUND("STREAM_NOT_FOUND"),
    EMPTY_TUPLE("EMPTY_TUPLE"),
    INVALID_TUPLE("INVALID_TUPLE"),
    NO_SUPPORT_STATEMENT_SUBGROUP("NO_SUPPORT_STATEMENT_SUBGROUP"),
    EMPTY_STATEMENTS("EMPTY_STATEMENTS"),
    NO_SUPPORT_STATEMENT_GROUP("NO_SUPPORT_STATEMENT_GROUP");

    private String messgae;

    public String getMessage() {
        return messgae;
    }

    private MessageEnum(String messgae) {
        this.messgae = messgae;
    }
}