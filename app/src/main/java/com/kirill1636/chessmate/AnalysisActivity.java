package com.kirill1636.chessmate;

import static android.graphics.Color.GREEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kirill1636.chessmate.model.Bishop;
import com.kirill1636.chessmate.model.Board;
import com.kirill1636.chessmate.model.Figure;
import com.kirill1636.chessmate.model.Game;
import com.kirill1636.chessmate.model.GameContext;
import com.kirill1636.chessmate.model.King;
import com.kirill1636.chessmate.model.Knight;
import com.kirill1636.chessmate.model.NewFigureType;
import com.kirill1636.chessmate.model.Pawn;
import com.kirill1636.chessmate.model.Queen;
import com.kirill1636.chessmate.model.Rook;
import com.kirill1636.chessmate.model.rest.ChessGame;
import com.kirill1636.chessmate.model.rest.User;
import com.kirill1636.chessmate.ui.MyCanvas;
import com.kirill1636.chessmate.ui.play.PlayFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AnalysisActivity extends AppCompatActivity {

    private MyCanvas canvas;

    private Board board;

    private List<String> movesSequence;

    private String myColor;

    private int curMove = 0;

    private ConstraintLayout root;

    private List<Figure> curFigs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);



        Intent intent = getIntent();
        ChessGame game = (ChessGame) intent.getSerializableExtra("games");
        User user = (User) intent.getSerializableExtra("user");
        setMyColor(user, game);
        movesSequence = parseGame(game.getMoves());
        board = new Board();
        board.setFields();
        canvas = new MyCanvas(this);
        canvas.setContext(new GameContext(board, null, null));
        root = findViewById(R.id.mainLayout);
        TextView view = new TextView(this);
        root.addView(view);

        view.setText("Соперник:" + getMyOpponentName(game, user));
        view.setX(50);
        view.setY(50);
        view.setTextSize(20);
        //setContentView(canvas);
        root.addView(canvas);
        setFigures(root);
        canvas.setImages();
        //canvas.invalidate();
        //canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
        canvas.setY(150);


        Button previous = new Button(this);
        previous.setText("<");
        previous.setX(275);
        previous.setY(1300);
        //back.setTextSize(30);
        //back.setBackgroundColor(GREEN);
        root.addView(previous);

        Button next = new Button(this);
        next.setText(">");
        next.setX(575);
        next.setY(1300);
        //back.setTextSize(30);
        //back.setBackgroundColor(GREEN);
        root.addView(next);

//        LinearLayout layout = findViewById(R.id.notationView_id);
//        Button button = new Button(layout.getContext());
//        Button button1 = new Button(layout.getContext());
//        button1.setY(100);
//        Button button2 = new Button(layout.getContext());
//        button2.setY(200);
//        Button button3 = new Button(layout.getContext());
//        button3.setY(300);
//        Button button4 = new Button(layout.getContext());
//        button4.setY(400);
//        Button button5 = new Button(layout.getContext());
//        button5.setY(500);
//        layout.addView(button);
//        layout.addView(button1);
//        layout.addView(button2);
//        layout.addView(button3);
//        layout.addView(button4);
//        layout.addView(button5);


//        ScrollView scrollView = new ScrollView(this);
//        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        LinearLayout layout = new LinearLayout(scrollView.getContext());
//        Button button = new Button(layout.getContext());
//        Button button1 = new Button(layout.getContext());
//        //button1.setY(100);
//        Button button2 = new Button(layout.getContext());
//        //button2.setY(200);
//        Button button3 = new Button(layout.getContext());
//        //button3.setY(300);
//        Button button4 = new Button(layout.getContext());
//        //button4.setY(400);
//        Button button5 = new Button(layout.getContext());
//        //button5.setY(500);
//        layout.addView(button);
//        layout.addView(button1);
//        layout.addView(button2);
//        layout.addView(button3);
//        layout.addView(button4);
//        layout.addView(button5);
//        scrollView.addView(layout);
//        root.addView(scrollView);
//        root.setY(1800);

        previous.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(curMove != 0){
                    curMove--;
                    reloadBoard();
                    playMoves(curMove);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(curMove != movesSequence.size()){
                    String move = movesSequence.get(curMove);
                    char fc = move.charAt(0);
                    if(Character.isUpperCase(fc)){
                        if(fc == 'S' || fc == 'L'){
                            makeMove(move.substring(1, 6));
                            makeMove(move.substring(7));
                        }
                        else if(fc == 'E'){
                            makeMove(move.substring(1, 6), move.substring(7));
                        }
                        else{
                            makeMove(move.substring(1, 6), fc, defineNewFigColor(curMove));
                        }
                    }
                    else{
                        makeMove(move);
                    }
                    curMove++;
                }
            }
        });
    }

    private void playMoves(int need){
        for (int i = 0; i < need; i++) {
            String move = movesSequence.get(i);
            char fc = move.charAt(0);
            if(Character.isUpperCase(fc)){
                if(fc == 'S' || fc == 'L'){
                    makeMove(move.substring(1, 6));
                    makeMove(move.substring(7));
                }
                else if(fc == 'E'){
                    makeMove(move.substring(1, 6), move.substring(7));
                }
                else{
                    makeMove(move.substring(1, 6), fc, defineNewFigColor(i));
                }
            }
            else{
                makeMove(move);
            }
        }
    }

    public void reloadBoard(){
        hideFigs();
        board.setFields();
        setFigures(root);

        canvas.setContext(new GameContext(board, null, null));
        canvas.setImages();
        canvas.invalidate();
    }

    public void setFigures(ConstraintLayout root){
        Figure[] whiteFigures = {new Rook("white", root.getContext()),
                new Knight("white", root.getContext()),
                new Bishop("white", root.getContext()),
                new Queen("white", root.getContext()),
                new King("white", root.getContext()),
                new Bishop("white", root.getContext()),
                new Knight("white", root.getContext()),
                new Rook("white", root.getContext())};

        Figure[] blackFigures = {new Rook("black", root.getContext()),
                new Knight("black", root.getContext()),
                new Bishop("black", root.getContext()),
                new Queen("black", root.getContext()),
                new King("black", root.getContext()),
                new Bishop("black", root.getContext()),
                new Knight("black", root.getContext()),
                new Rook("black", root.getContext())};

        curFigs.addAll(Arrays.asList(whiteFigures));
        curFigs.addAll(Arrays.asList(blackFigures));
        for (int i = 0; i < whiteFigures.length; i++) {
            Figure currentFig = whiteFigures[i];
            Pawn whitePawn = new Pawn("white", -1, root.getContext());
            board.setCell(7, i, currentFig);
            board.setCell(6, i, whitePawn);
            root.addView(currentFig.getMyImage());
            root.addView(whitePawn.getMyImage());
            curFigs.add(whitePawn);
        }
        for (int i = 0; i < blackFigures.length; i++) {
            Figure currentFig = blackFigures[i];
            Pawn blackPawn = new Pawn("black", 1, root.getContext());
            board.setCell(0, i, currentFig);
            board.setCell(1, i, blackPawn);
            root.addView(currentFig.getMyImage());
            root.addView(blackPawn.getMyImage());
            curFigs.add(blackPawn);
        }

        if(myColor.equals("black")){
            board.turnBoard();
        }
    }

    private String getMyOpponentName(ChessGame chessGame, User user){
        if(Objects.equals(user.getId(), chessGame.getWinner().getId())){
            return chessGame.getLoser().getName();
        }
        else{
            return chessGame.getWinner().getName();
        }
    }

    private List<String> parseGame(String gameNotation){
        List<String> moves = new ArrayList<>();
        StringBuilder curMove = new StringBuilder();
        for (int i = 0; i < gameNotation.length(); i++){
            if(gameNotation.charAt(i) == '|'){
                moves.add(curMove.toString());
                curMove = new StringBuilder();
            }
            else{
                curMove.append(gameNotation.charAt(i));
            }
        }
        return moves;
    }

    private void makeMove(String move){
    Figure myCell = board.getFields().get(getRealCoordinate(move.charAt(0))).get(getRealCoordinate(move.charAt(1)));
    board.setCell(getRealCoordinate(move.charAt(0)), getRealCoordinate(move.charAt(1)), null);
    Figure nextCell = board.getFields().get(getRealCoordinate(move.charAt(3))).get(getRealCoordinate(move.charAt(4)));
    if(nextCell != null){
        curFigs.remove(nextCell);
        nextCell.getMyImage().setVisibility(View.INVISIBLE);
    }
    board.setCell(getRealCoordinate(move.charAt(3)), getRealCoordinate(move.charAt(4)), myCell);

    canvas.setContext(new GameContext(board, null, null));
    canvas.setImages();
    canvas.invalidate();
    }

    private void makeMove(String move, String ep){
        Figure myCell = board.getFields().get(getRealCoordinate(move.charAt(0))).get(getRealCoordinate(move.charAt(1)));
        board.setCell(getRealCoordinate(move.charAt(0)), getRealCoordinate(move.charAt(1)), null);
        board.setCell(getRealCoordinate(move.charAt(3)), getRealCoordinate(move.charAt(4)), myCell);
        Figure epCell = board.getFields().get(getRealCoordinate(ep.charAt(0))).get(getRealCoordinate(ep.charAt(1)));
        epCell.getMyImage().setVisibility(View.INVISIBLE);
        curFigs.remove(epCell);
        board.setCell(getRealCoordinate(ep.charAt(0)), getRealCoordinate(ep.charAt(1)), null);

        canvas.setContext(new GameContext(board, null, null));
        canvas.setImages();
        canvas.invalidate();
    }

    private void makeMove(String move, char newFig, String figColor){
        Figure nF = null;
        if(newFig == 'Q') {
            nF = new Queen(figColor, root.getContext());
        }
        else if (newFig == 'R') {
            nF = new Rook(figColor, root.getContext());
        }
        else if (newFig == 'B') {
            nF = new Bishop(figColor, root.getContext());
        }
        else if (newFig == 'K') {
            nF = new Knight(figColor, root.getContext());
        }
        root.addView(nF.getMyImage());
        Figure myCell = board.getFields().get(getRealCoordinate(move.charAt(0))).get(getRealCoordinate(move.charAt(1)));
        myCell.getMyImage().setVisibility(View.INVISIBLE);
        board.setCell(getRealCoordinate(move.charAt(0)), getRealCoordinate(move.charAt(1)), null);
        Figure nextCell = board.getFields().get(getRealCoordinate(move.charAt(3))).get(getRealCoordinate(move.charAt(4)));
        if(nextCell != null){
            nextCell.getMyImage().setVisibility(View.INVISIBLE);
            curFigs.remove(nextCell);
        }
        board.setCell(getRealCoordinate(move.charAt(3)), getRealCoordinate(move.charAt(4)), nF);

        canvas.setContext(new GameContext(board, null, null));
        canvas.setImages();
        canvas.invalidate();
    }

    private int getRealCoordinate(char c){
        int cord = Character.getNumericValue(c);
        if(myColor.equals("black")){
            cord = 7 - cord;
        }
        return cord;
    }

    private void hideFigs(){
        for (Figure fig: curFigs) {
            //fig.getMyImage().setVisibility(View.INVISIBLE);
            root.removeView(fig.getMyImage());
        }
        curFigs = new ArrayList<>();
    }

    private void setMyColor(User user, ChessGame game){
        if(!Objects.equals(user.getId(), game.getMovedFirst())){
            myColor = "black";
        }
        else {
            myColor = "white";
        }
    }
    private String defineNewFigColor(int moveI){
        if(moveI % 2 == 0){
            return "white";
        }
        else {
            return "black";
        }
    }
}