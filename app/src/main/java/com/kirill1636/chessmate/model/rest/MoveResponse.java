package com.kirill1636.chessmate.model.rest;

import com.kirill1636.chessmate.model.TieType;

public class MoveResponse {
    private AfterMoveStatus status;
    private TieType tieType;

    private int newRating;

    public MoveResponse(){

    }

    public MoveResponse(AfterMoveStatus status, TieType tieType, int newRating) {
        this.status = status;
        this.tieType = tieType;
        this.newRating = newRating;
    }

    public int getNewRating() {
        return newRating;
    }

    public void setNewRating(int newRating) {
        this.newRating = newRating;
    }

    public AfterMoveStatus getStatus() {
        return status;
    }

    public void setStatus(AfterMoveStatus status) {
        this.status = status;
    }

    public TieType getTieType() {
        return tieType;
    }

    public void setTieType(TieType tieType) {
        this.tieType = tieType;
    }
}
