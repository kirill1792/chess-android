package com.kirill1636.chessmate.ui.gallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kirill1636.chessmate.AnalysisActivity;
import com.kirill1636.chessmate.GameActivity;
import com.kirill1636.chessmate.LoginActivity;
import com.kirill1636.chessmate.R;
import com.kirill1636.chessmate.databinding.FragmentGalleryBinding;
import com.kirill1636.chessmate.model.rest.AfterMoveStatus;
import com.kirill1636.chessmate.model.rest.ChessGame;
import com.kirill1636.chessmate.model.rest.Move;
import com.kirill1636.chessmate.model.rest.MoveResponse;
import com.kirill1636.chessmate.model.rest.User;
import com.kirill1636.chessmate.service.RestClientService;
import com.kirill1636.chessmate.ui.play.PlayFragment;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GameActivity gameActivity;

    private RestClientService restClientService = new RestClientService();

    private User myUser;
    private ListView listView;

    private List<ChessGame> chessGames = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gameActivity = (GameActivity) container.getContext();
        Map<String, Object> activityData = gameActivity.getActivityData();
        myUser = (User) activityData.get("user");

          listView = root.findViewById(R.id.listView);

        GetGamesTask getGamesTask = new GetGamesTask();
        getGamesTask.execute(myUser.getId());

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



//        list.add("Вася     11-04-2024 13:36:46     Победа");
//        list.add("Петя     12-04-2024 14:29:02     Поражение");
//        list.add("Саша");
//        list.add("Коля");
//        list.add("Овощ");
//        list.add("Вася     11-04-2024 13:36:46     Победа");
//        list.add("Петя     12-04-2024 14:29:02     Поражение");
//        list.add("Саша");
//        list.add("Коля");
//        list.add("Овощ");
//        list.add("Вася     11-04-2024 13:36:46     Победа");
//        list.add("Петя     12-04-2024 14:29:02     Поражение");
//        list.add("Саша");
//        list.add("Коля");
//        list.add("Овощ");
//        list.add("Вася     11-04-2024 13:36:46     Победа");
//        list.add("Петя     12-04-2024 14:29:02     Поражение");
//        list.add("Саша");
//        list.add("Коля");
//        list.add("Овощ");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AnalysisActivity.class);
                intent.putExtra("games", chessGames.get(position));
                intent.putExtra("user", myUser);
                startActivity(intent);
            }
        });

        return root;
    }

    private class GetGamesTask extends AsyncTask<Integer, String, List<ChessGame>> {
        @Override
        protected List<ChessGame> doInBackground(Integer... params) {
            try {
                return restClientService.getGames(params[0]);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChessGame> games) {
            List<String> viewGames = new ArrayList<>();
            for (ChessGame game : games) {
                chessGames.add(game);
                String gameRes = "Ничья";
                String opponentName = game.getWinner().getName();
                if(game.getWinner().getId() == myUser.getId()){
                    opponentName = game.getLoser().getName();
                }
                if(!game.isTie()){
                    if(game.getWinner().getId() == myUser.getId()){
                        gameRes = "Победа";
                    }
                    else {
                        gameRes = "Поражение";
                    }
                }
                viewGames.add(opponentName + "     " + game.getDate() + "     " + gameRes);
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, viewGames);
            listView.setAdapter(arrayAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}