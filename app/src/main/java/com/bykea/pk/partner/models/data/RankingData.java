package com.bykea.pk.partner.models.data;

import com.bykea.pk.partner.models.response.CommonResponse;

import java.util.List;

public class RankingData {
    String _id;

    private List<WeeklyBonus> weeklyBonus;

    private List<RankingPosition> position;

    private List<Ranking> ranking;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<WeeklyBonus> getWeeklyBonus() {
        return weeklyBonus;
    }

    public void setWeeklyBonus(List<WeeklyBonus> weeklyBonus) {
        this.weeklyBonus = weeklyBonus;
    }

    public List<RankingPosition> getPosition() {
        return position;
    }

    public void setPosition(List<RankingPosition> position) {
        this.position = position;
    }

    public List<Ranking> getRanking() {
        return ranking;
    }

    public void setRanking(List<Ranking> ranking) {
        this.ranking = ranking;
    }
}
