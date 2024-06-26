package com.kirill1636.chessmate.model.rest;

import com.kirill1636.chessmate.model.NewFigureType;
import com.kirill1636.chessmate.model.TieType;

public class GetMove {

    private String moveFrom;
    private String moveTo;
    private AfterMoveStatus status;
    private int id;
    private NewFigureType figureType;
    private String rookFrom;
    private String rookTo;
    private TieType tieType;

    private int newRating;

    public GetMove(){

    }

    public GetMove(String moveFrom, String moveTo, AfterMoveStatus status, int id, NewFigureType figureType, String rookFrom, String rookTo, TieType tieType, int newRating){
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
        this.status = status;
        this.id = id;
        this.figureType = figureType;
        this.rookFrom = rookFrom;
        this.rookTo = rookTo;
        this.tieType = tieType;
        this.newRating = newRating;
    }

    public int getNewRating() {
        return newRating;
    }

    public void setNewRating(int newRating) {
        this.newRating = newRating;
    }

    public TieType getTieType() {
        return tieType;
    }

    public void setTieType(TieType tieType) {
        this.tieType = tieType;
    }

    public String getRookFrom() {
        return rookFrom;
    }

    public void setRookFrom(String rookFrom) {
        this.rookFrom = rookFrom;
    }

    public String getRookTo() {
        return rookTo;
    }

    public void setRookTo(String rookTo) {
        this.rookTo = rookTo;
    }

    public NewFigureType getFigureType() {
        return figureType;
    }

    public void setFigureType(NewFigureType figureType) {
        this.figureType = figureType;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoveFrom() {
        return moveFrom;
    }

    public void setMoveFrom(String moveFrom) {
        this.moveFrom = moveFrom;
    }

    public String getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(String moveTo) {
        this.moveTo = moveTo;
    }

    public AfterMoveStatus getStatus() {
        return status;
    }

    public void setStatus(AfterMoveStatus status) {
        this.status = status;
    }

}
