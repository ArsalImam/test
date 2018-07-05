package com.bykea.pk.partner.models.data;

public class RankingStatsTypeModel {

    private String statsDriver1;
    private String statsDriver2;
    private String statsDriver3;
    private String statsType;

    public RankingStatsTypeModel(String statsDriver1, String statsDriver2, String statsDriver3, String statsType) {
        this.statsDriver1 = statsDriver1;
        this.statsDriver2 = statsDriver2;
        this.statsDriver3 = statsDriver3;
        this.statsType = statsType;
    }

    public String getStatsDriver1() {
        return statsDriver1;
    }

    public void setStatsDriver1(String statsDriver1) {
        this.statsDriver1 = statsDriver1;
    }

    public String getStatsDriver2() {
        return statsDriver2;
    }

    public void setStatsDriver2(String statsDriver2) {
        this.statsDriver2 = statsDriver2;
    }

    public String getStatsDriver3() {
        return statsDriver3;
    }

    public void setStatsDriver3(String statsDriver3) {
        this.statsDriver3 = statsDriver3;
    }

    public String getStatsType() {
        return statsType;
    }

    public void setStatsType(String statsType) {
        this.statsType = statsType;
    }
}
