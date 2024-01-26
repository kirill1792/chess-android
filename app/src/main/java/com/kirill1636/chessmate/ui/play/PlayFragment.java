package com.kirill1636.chessmate.ui.play;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kirill1636.chessmate.R;
import com.kirill1636.chessmate.databinding.FragmentPlayBinding;
import com.kirill1636.chessmate.model.Bishop;
import com.kirill1636.chessmate.model.Coordinates;
import com.kirill1636.chessmate.model.Game;
import com.kirill1636.chessmate.model.GameContext;
import com.kirill1636.chessmate.model.Knight;
import com.kirill1636.chessmate.model.MoveResult;
import com.kirill1636.chessmate.model.NewFigureType;
import com.kirill1636.chessmate.model.Parser;
import com.kirill1636.chessmate.model.Queen;
import com.kirill1636.chessmate.model.Rook;
import com.kirill1636.chessmate.model.Status;
import com.kirill1636.chessmate.model.TieType;
import com.kirill1636.chessmate.model.rest.AfterMoveStatus;
import com.kirill1636.chessmate.model.rest.GameInitData;
import com.kirill1636.chessmate.model.rest.GetMove;
import com.kirill1636.chessmate.model.rest.Move;
import com.kirill1636.chessmate.model.rest.MoveResponse;
import com.kirill1636.chessmate.ui.MyCanvas;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayFragment extends Fragment {

    private FragmentPlayBinding binding;
    private EditText nameArea;
    private MyCanvas canvas;
    private Game myGame;
    private ConstraintLayout root;
    private HashMap<Integer, Class> figureChooseMap = new HashMap<>();
    private int id;
    private View testView;
    private LinearLayout electionArea;
    //private final String SERVER_HOST = "10.0.2.2";
    private final String SERVER_HOST = "188.225.33.112";
    private final String SERVER_URL = "http://" + SERVER_HOST + ":8080/";
    private Socket socket;
    private DataOutputStream socketOut;
    private DataInputStream socketIn;
    private ObjectMapper objectMapper = new ObjectMapper();
    private LayoutInflater inflater;
    private ViewGroup viewGroup;
    private HashMap<TieType, Integer> tieTexts = new HashMap<>();
    private HashMap<Integer, Integer> endGameMap = new HashMap<>();
    private boolean isGameOver = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlayViewModel homeViewModel =
                new ViewModelProvider(this).get(PlayViewModel.class);
        this.inflater = inflater;
        this.viewGroup = container;
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        this.root = root;
        nameArea = root.findViewById(R.id.playerName);
        //myGame = new Game("black");
        //myGame.placePieces(root);
        //canvas = root.findViewById(R.id.myCanvas);
        //canvas.setOnTouchListener(handleTouch);
        //canvas.setContext(new GameContext(myGame.getBoard(), myGame.getSelectedFigure(), new ArrayList<>()));
        //canvas.setImages();
        Button button = (Button) root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameArea.getText().toString();
                root.removeView(button);
                root.removeView(nameArea);
                TextView view = new TextView(getActivity());
                id = view.generateViewId();
                view.setId(id);

                root.addView(view);
                view.setText("Поиск игры...");
                view.setX(280);
                view.setY(550);
                view.setTextSize(30);
//                ConstraintLayout.LayoutParams lp =
//                        (ConstraintLayout.LayoutParams)
//                                view.getLayoutParams();
//
//                // Set width in LayoutParams in pixels
//                lp.width = 500;
//                lp.height = 200;
//
//                // Apply the updated layout parameters to TextView
//                view.setLayoutParams(lp);
                FindGameTask task = new FindGameTask();
                task.execute(name);
            }
        });
        return root;
    }

//    public void buttonListener(View view) {
//        nameArea.append("Его имя Роберт Полсен");
//    }

    private void refresh() {
        getFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, new PlayFragment()).commit();
    }

    private class FindGameTask extends AsyncTask<String, String, GameInitData> {
        @Override
        protected GameInitData doInBackground(String... params) {
            try {
                final String url =  SERVER_URL + "play?name={name}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                GameInitData res = restTemplate.getForObject(url, GameInitData.class, params[0]);
                /*ResponseEntity<String> res = restTemplate.exchange(
                        url, HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class, params[0]);*/
                socket = new Socket(SERVER_HOST, 9075);
                socketOut = new DataOutputStream(socket.getOutputStream());
                socketIn = new DataInputStream(socket.getInputStream());
                socketOut.writeUTF(String.valueOf(res.getPlayerId()));
                return res;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(GameInitData gameInitData) {
            TextView view = new TextView(getActivity());
            root.addView(view);
            root.removeView(root.findViewById(id));
            view.setText("Соперник:" + gameInitData.getOpponentName());
            view.setX(50);
            view.setY(50);
            view.setTextSize(20);

            tieTexts.put(TieType.STALEMATE, R.string.stalemate);
            tieTexts.put(TieType.AGREEMENT, R.string.agreement);
            tieTexts.put(TieType.LACK_OF_FIGS, R.string.lack_of_figs);
            tieTexts.put(TieType.REPETITION, R.string.repetition);
            tieTexts.put(TieType.MOVES_LIMITED, R.string.moves_limited);

            endGameMap.put(R.string.win, R.color.green_win);
            endGameMap.put(R.string.lose, R.color.red_lose);
            endGameMap.put(R.string.tie, R.color.grey_tie);

            myGame = new Game(gameInitData.getColor());
            myGame.setMyId(gameInitData.getPlayerId());
            myGame.setGameId(gameInitData.getGameId());
            myGame.setOpponentName(gameInitData.getOpponentName());
            canvas = new MyCanvas(getActivity());
            canvas.setContext(new GameContext(myGame.getBoard(), myGame.getSelectedFigure(), new ArrayList<>()));
            root.addView(canvas);
            myGame.placePieces(root);
            canvas.setImages();
            //canvas.invalidate();
            //canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
            canvas.setY(150);
            canvas.setOnTouchListener(handleTouch);
            if(gameInitData.getColor().equals("black")){
                myGame.setMyMove(false);
                GetMoveTask task = new GetMoveTask();
                task.execute(myGame.getGameId(), myGame.getMyId());
            }
//            View view1 = new View(getActivity());
//            testView = view1;
//            view1.setBackgroundColor(0x0000FF00);
//
//            view1.setOnTouchListener(handleView);
//            //view.setX(50);
//            //view.setY(50);
//            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
//                    (ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//            view1.setLayoutParams(layoutParams);
//            root.addView(view1);


//
//            LinearLayout electionAreaLocal = new LinearLayout(getActivity());
//            electionAreaLocal.setOrientation(LinearLayout.VERTICAL);
//            electionAreaLocal.setBackgroundColor(Color.WHITE);
//            electionArea = electionAreaLocal;
//            //electionAreaLocal.setOnTouchListener(handleElectionArea);
//            electionAreaLocal.setX(300);
//            electionAreaLocal.setY(400);
////            ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams
////                    (400, 400);
//            //electionAreaLocal.setLayoutParams(layoutParams1);
//            root.addView(electionAreaLocal);
//            String url = "@drawable/white_queen";
//            ImageView i = new ImageView(getActivity());
//            int imageResource = getActivity().getResources().getIdentifier(
//                    url, null, getActivity().getPackageName());
//            Drawable drawable = getActivity().getResources().getDrawable(imageResource);
//            i.setImageDrawable(drawable);
//            // set the ImageView bounds to match the Drawable's dimensions
//            i.setAdjustViewBounds(true);
//            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(150, 150);
//            i.setLayoutParams(imageParams);
//            electionAreaLocal.addView(i);
//
//            String url1 = "@drawable/white_knight";
//            ImageView i1 = new ImageView(getActivity());
//            int imageResource1 = getActivity().getResources().getIdentifier(
//                    url1, null, getActivity().getPackageName());
//            Drawable drawable1 = getActivity().getResources().getDrawable(imageResource1);
//            i1.setImageDrawable(drawable1);
//            // set the ImageView bounds to match the Drawable's dimensions
//            i1.setAdjustViewBounds(true);
//            LinearLayout.LayoutParams imageParams1 = new LinearLayout.LayoutParams(150, 150);
//            i1.setLayoutParams(imageParams1);
//            electionAreaLocal.addView(i1);
//
//            String url2 = "@drawable/white_rook";
//            ImageView i2 = new ImageView(getActivity());
//            int imageResource2 = getActivity().getResources().getIdentifier(
//                    url2, null, getActivity().getPackageName());
//            Drawable drawable2 = getActivity().getResources().getDrawable(imageResource2);
//            i2.setImageDrawable(drawable2);
//            // set the ImageView bounds to match the Drawable's dimensions
//            i2.setAdjustViewBounds(true);
//            LinearLayout.LayoutParams imageParams2 = new LinearLayout.LayoutParams(150, 150);
//            i2.setLayoutParams(imageParams2);
//            electionAreaLocal.addView(i2);
//
//            String url3 = "@drawable/white_bishop";
//            ImageView i3 = new ImageView(getActivity());
//            int imageResource3 = getActivity().getResources().getIdentifier(
//                    url3, null, getActivity().getPackageName());
//            Drawable drawable3 = getActivity().getResources().getDrawable(imageResource3);
//            i3.setImageDrawable(drawable3);
//            // set the ImageView bounds to match the Drawable's dimensions
//            i3.setAdjustViewBounds(true);
//            LinearLayout.LayoutParams imageParams3 = new LinearLayout.LayoutParams(150, 150);
//            i3.setLayoutParams(imageParams3);
//            electionAreaLocal.addView(i3);
            //i.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
//            if(gameInitData.getColor().equals("black")){
//                myGame.setMyMove(false);
//                GetMoveTask task = new GetMoveTask();
//                task.execute(myGame.getGameId(), myGame.getMyId());
//            }
        }
    }


    private class SendMoveTask extends AsyncTask<Move, String, MoveResponse> {
        @Override
        protected MoveResponse doInBackground(Move... params) {
            try {
                final String url = SERVER_URL + "move";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                MoveResponse res = restTemplate.postForObject(url, params[0], MoveResponse.class);
                /*ResponseEntity<String> res = restTemplate.exchange(
                        url, HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class, params[0]);*/
                return res;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(MoveResponse moveResponse) {
            int a = 0;
            if (moveResponse.getStatus() == AfterMoveStatus.CHECKMATE){
                preProcessGameOver(R.string.win, myGame.getOpponentName());
                isGameOver = true;
            }
            else if(moveResponse.getStatus() == AfterMoveStatus.TIE) {
                preProcessGameOver(R.string.tie, moveResponse.getTieType());
                isGameOver = true;
            }
            else {
                GetMoveTask task = new GetMoveTask();
                task.execute(myGame.getGameId(), myGame.getMyId());
            }
        }
    }

    private class GetMoveTask extends AsyncTask<Integer, String, GetMove> {
        @Override
        protected GetMove doInBackground(Integer... params) {
            try {
                final String url =  SERVER_URL + "move?gameId={gameId}&playerId={playerId}";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
               // GetMove res = restTemplate.getForObject(url, GetMove.class, params[0], params[1]);
                String getMoveJson = socketIn.readUTF();
                GetMove res = objectMapper.readValue(getMoveJson, GetMove.class);
                socketOut.writeUTF("OK");
                /*ResponseEntity<String> res = restTemplate.exchange(
                        url, HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class, params[0]);*/
                return res;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(GetMove move) {
            myGame.opponentMove(move.getMoveFrom(), move.getMoveTo(), move.getFigureType(), move.getRookFrom(), move.getRookTo(), root);
            canvas.setContext(new GameContext(myGame.getBoard(), null, new ArrayList<>()));
            canvas.setImages();
            canvas.invalidate();
            if (move.getStatus() == AfterMoveStatus.CHECKMATE){
                preProcessGameOver(R.string.lose, myGame.getOpponentName());
                isGameOver = true;
            }
            if(move.getStatus() == AfterMoveStatus.TIE){
                preProcessGameOver(R.string.tie, move.getTieType());
                isGameOver = true;
            }
        }
    }

    public void setFigureElection(){
        View view1 = new View(getActivity());
        testView = view1;
        view1.setBackgroundColor(0x0000FF00);
        view1.setOnTouchListener(handleView);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        view1.setLayoutParams(layoutParams);
        root.addView(view1);

        LinearLayout electionAreaLocal = new LinearLayout(getActivity());
        electionAreaLocal.setOrientation(LinearLayout.VERTICAL);
        electionAreaLocal.setBackgroundColor(Color.WHITE);
        electionArea = electionAreaLocal;
        electionAreaLocal.setX(300);
        electionAreaLocal.setY(400);
        root.addView(electionAreaLocal);

        Class[] figures = {Queen.class, Rook.class, Bishop.class, Knight.class};
        for (Class figureType : figures) {
            String url = "@drawable/" + myGame.getMyColor() + "_" + figureType.getSimpleName().toLowerCase();
            ImageView i = new ImageView(getActivity());
            id = i.generateViewId();
            i.setId(id);
            figureChooseMap.put(id, figureType);
            int imageResource = getActivity().getResources().getIdentifier(
                    url, null, getActivity().getPackageName());
            Drawable drawable = getActivity().getResources().getDrawable(imageResource);
            i.setImageDrawable(drawable);
            i.setAdjustViewBounds(true);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(150, 150);
            i.setOnTouchListener(figureImagesListener);
            i.setLayoutParams(imageParams);
            electionArea.addView(i);
        }
    }

    public void preProcessGameOver(int gameResult, TieType tieType){
        gameOver(gameResult, getString(tieTexts.get(tieType)));
    }

    public void preProcessGameOver(int gameResult, String name){
        if (gameResult == R.string.win){
            gameOver(gameResult, getString(R.string.checkmate_win, name));
        }
        else if(gameResult == R.string.lose){
            gameOver(gameResult, getString(R.string.checkmate_lose, name));
        }
    }

    public void gameOver(int gameResult, String mainText){
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                (700, 1000);
        ConstraintLayout.LayoutParams viewParams = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        viewParams.setMargins(10, 10, 10, 0);


        RelativeLayout endGameArea = new RelativeLayout(getActivity());
        View view = new View(getActivity());

        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;

        endGameArea.setBackgroundColor(Color.WHITE);

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(10, 0xFF000000); //black border with full opacity

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(200, 100);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.topMargin = 700;
        Button leaveGame = new Button(getActivity());
        leaveGame.setLayoutParams(buttonParams);
        leaveGame.setBackgroundColor(getContext().getResources().getColor(R.color.leave_game_button));
        leaveGame.setGravity(Gravity.CENTER);
        leaveGame.setText("ОК");
        endGameArea.addView(leaveGame);

        endGameArea.setLayoutParams(layoutParams);
        view.setLayoutParams(viewParams);
        view.setBackgroundColor(getContext().getResources().getColor(endGameMap.get(gameResult)));
        endGameArea.addView(view);
        endGameArea.setBackground(border);
        endGameArea.addView(createLabel(gameResult));
        endGameArea.addView(createText(mainText));
        root.addView(endGameArea);

        leaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }



    private TextView createLabel(int textId){
        RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(500, 150);
        labelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        labelParams.topMargin = 40;
        TextView label = new TextView(getActivity());
        label.setGravity(Gravity.CENTER);
        //label.setBackgroundColor(Color.BLUE);
        label.setLayoutParams(labelParams);
        label.setText(textId);
        label.setTextSize(30);
        label.setTextColor(Color.BLACK);
        return label;
    }

    private TextView createText(String mainText){
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(500, 200);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.topMargin = 400;
        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        //text.setBackgroundColor(Color.BLUE);
        text.setLayoutParams(textParams);
        text.setText(mainText);
        text.setTextSize(20);
        text.setTextColor(Color.BLACK);
        return text;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
//                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
//                            (700, 1000);
//                    ConstraintLayout.LayoutParams viewParams = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
//                    viewParams.setMargins(10, 10, 10, 0);
//                    RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(500, 150);
//                    labelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                    labelParams.topMargin = 40;
//                    RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(500, 200);
//                    textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                    textParams.topMargin = 400;
//                    RelativeLayout endGameArea = new RelativeLayout(getActivity());
//                    View view = new View(getActivity());
//                    layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
//                    layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
//                    layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
//                    layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
//                    endGameArea.setBackgroundColor(Color.WHITE);
//
//                    GradientDrawable border = new GradientDrawable();
//                    border.setColor(0xFFFFFFFF); //white background
//                    border.setStroke(10, 0xFF000000); //black border with full opacity
//
//                    TextView label = new TextView(getActivity());
////                    label.setX(150);
////                    label.setY(100);
////                    textParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
////                    textParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
//                    label.setGravity(Gravity.CENTER);
//                    //label.setBackgroundColor(Color.BLUE);
//                    label.setLayoutParams(labelParams);
//                    label.setText(R.string.win);
//                    label.setTextSize(30);
//                    label.setTextColor(Color.BLACK);
//
//                    TextView text = new TextView(getActivity());
////                    label.setX(150);
////                    label.setY(100);
////                    textParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
////                    textParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
//                    text.setGravity(Gravity.CENTER);
//                    //text.setBackgroundColor(Color.BLUE);
//                    text.setLayoutParams(textParams);
//                    text.setText(getString(R.string.checkmate_lose, "Лютый овощ"));
//                    text.setTextSize(20);
//                    text.setTextColor(Color.BLACK);
//
//                    endGameArea.setLayoutParams(layoutParams);
//                    view.setLayoutParams(viewParams);
//                    view.setBackgroundColor(getContext().getResources().getColor(R.color.red_lose));
//                    endGameArea.addView(view);
//                    endGameArea.setBackground(border);
//                    endGameArea.addView(label);
//                    endGameArea.addView(text);
//                    root.addView(endGameArea);
//                    LinearLayout.LayoutParams linLayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
//                    linLayParams.setMargins(10, 10, 10, 0);
//                    LinearLayout.LayoutParams borderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                    layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
//                    layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
//                    layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
//                    layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
//
////                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(100, 100);
////                    textParams.setMargins(0, 100, 0, 0);
////                    textParams.gravity = Gravity.CENTER_HORIZONTAL;
//
//
//                    LinearLayout endGameArea = new LinearLayout(getActivity());
//                    View view = new View(getActivity());
//                    TextView label = new TextView(getActivity());
//                    label.setX(150);
//                    label.setY(200);
//                   // label.setLayoutParams(textParams);
//                    label.setText("ryrdjysetugf");
//                    label.setTextSize(30);
//                    //View borderView = new View(getActivity());
//                    //endGameArea.setOrientation(LinearLayout.VERTICAL);
//                    //electionArea = endGameArea;
//                    //endGameArea.setGravity(Gravity.CENTER_HORIZONTAL);
////                    endGameArea.setX(300);
////                    endGameArea.setY(500);
//                    endGameArea.setBackgroundColor(Color.WHITE);
//                    GradientDrawable border = new GradientDrawable();
//                    border.setColor(0xFFFFFFFF); //white background
//                    border.setStroke(10, 0xFF000000); //black border with full opacity
////
////                    GradientDrawable border1 = new GradientDrawable();
////                    //border1.setColor(0xFFFFFFFF); //white background
////                    border1.setStroke(10, 0xFF000000); //black border with full opacity
////
//                    endGameArea.setLayoutParams(layoutParams);
//                    view.setLayoutParams(linLayParams);
////                    //borderView.setLayoutParams(borderParams);
////                    //endGameArea.addView(borderView);'
//                    view.setBackgroundColor(getContext().getResources().getColor(R.color.red_loss));
//                    endGameArea.addView(view);
////                    view.setBackground(border1);
//                    endGameArea.setBackground(border);
//                    endGameArea.addView(label);
//
//
//                    root.addView(endGameArea);
                    if (checkCords(x, y) && myGame.isMyMove() && !isGameOver) {
                        MoveResult moveResult = myGame.processCords(new Coordinates(y / (canvas.getWidth() / 8), x / (canvas.getWidth() / 8)));
                        System.out.println(moveResult);
                        canvas.setContext(new GameContext(myGame.getBoard(), myGame.getSelectedFigure(), moveResult.coordinates));
                        canvas.setImages();
                        canvas.invalidate();
                        if(moveResult.status == Status.MOVED) {
                            Parser parser = new Parser(myGame.getMyColor());
                            SendMoveTask task = new SendMoveTask();
                            task.execute(new Move(parser.notate(moveResult.moveFrom), parser.notate(moveResult.moveTo), myGame.getMyId(), myGame.getGameId(), null));
                        }
                        else if (moveResult.status == Status.NEW_FIGURE) {
                            setFigureElection();
                        }
                    }
                    break;
            }
            return true;
        }
    };

    private final View.OnTouchListener handleView = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    root.removeView(electionArea);
                    root.removeView(testView);
                    figureChooseMap = null;
                    break;
            }
            return true;
        }
    };

    private final View.OnTouchListener figureImagesListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    Class figureType = figureChooseMap.get(v.getId());
                    NewFigureType type = myGame.moveAndPlaceNewFigure(figureType, root);
                    root.removeView(electionArea);
                    root.removeView(testView);
                    figureChooseMap = null;
                    canvas.setContext(new GameContext(myGame.getBoard(), myGame.getSelectedFigure(), new ArrayList<>()));
                    canvas.setImages();
                    canvas.invalidate();

                    Parser parser = new Parser(myGame.getMyColor());
                    SendMoveTask task = new SendMoveTask();
                    task.execute(new Move(parser.notate(myGame.pawnCords), parser.notate(myGame.pawnMoveToCords), myGame.getMyId(), myGame.getGameId(), type));
                    break;
            }
            //electionArea.setBackgroundColor(Color.BLUE);
            return true;
        }
    };

    private boolean checkCords(int x, int y){
        return y < canvas.getWidth();
    }
}

// From create view
//        // Instantiate an ImageView and define its properties
//        ImageView i = new ImageView(root.getContext());
//        int imageResource = getResources().getIdentifier(
//                "@drawable/black_knight", null, root.getContext().getPackageName());
//        Drawable drawable = getResources().getDrawable(imageResource);
//        i.setImageDrawable(drawable);
//        //root.getContext().getResources();
//        // set the ImageView bounds to match the Drawable's dimensions
//        i.setAdjustViewBounds(true);
//        i.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
//        i.setX(0);
//        i.setY(65);
//        // Add the ImageView to the layout and set the layout as the content view.
//        root.addView(i);
       /*
        ImageView imageView = new ImageView(root.getContext());
        InputStream stream = null;
        try {
            stream = root.getContext().getAssets().open("black_bishop.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(stream, null);
        imageView.setImageDrawable(d);
        //imageView.setVisibility();
        View inflatedView = View.inflate(canvas.getContext(), canvas., root);
        inflatedView.addView*/
//root.addView(imageView);
//final TextView textView = binding.textHome;
//homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
