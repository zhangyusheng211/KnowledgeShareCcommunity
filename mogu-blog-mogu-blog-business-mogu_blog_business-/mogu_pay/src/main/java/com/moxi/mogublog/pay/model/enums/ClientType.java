package com.moxi.mogublog.pay.model.enums;

/**
 * 支付客户端类型
 *
 * @author 遇见
 */
public enum ClientType {
    //todo 后续其他端使用配置读取写在这里
    /**
     * pc客户端
     */
    PC("pc_config", "PC");


    private String dbColumn;
    private String client;

    ClientType(String dbColumn, String client) {
        this.dbColumn = dbColumn;
        this.client = client;

    }

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String value() {
        return this.name();
    }


}
