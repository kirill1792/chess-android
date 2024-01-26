package com.kirill1636.chessmate.model;

public class Parser {

    private String boardNums = "87654321";
    private String boardLetters = "abcdefgh";

    public Parser(String color){
        if (color.equals("black")){
            boardNums = new StringBuilder(boardNums).reverse().toString();
            boardLetters = new StringBuilder(boardLetters).reverse().toString();
        }
    }

    public Coordinates parse(String move){
        int column = boardLetters.indexOf(move.charAt(0));
        int row = boardNums.indexOf(move.charAt(1));
        return new Coordinates(row, column);
    }

    public String notate(Coordinates coordinates){
        return "" + boardLetters.charAt(coordinates.getColumn()) + boardNums.charAt(coordinates.getRow());
    }
}
