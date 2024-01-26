package com.kirill1636.chessmate.model;

import java.util.List;

public class GameContext {
    private Board board;
    private Figure selectedFigure;
    private List<Coordinates> coordinates;

    public GameContext(Board board, Figure selectedFigure, List<Coordinates> coordinates){
        this.board = board;
        this.selectedFigure = selectedFigure;
        this.coordinates = coordinates;
    }

    public Board getBoard() {
        return board;
    }

    public Figure getSelectedFigure() {
        return selectedFigure;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }
}
