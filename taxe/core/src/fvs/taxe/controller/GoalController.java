package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.dialog.GoalClickListener;
import gameLogic.Player;
import gameLogic.PlayerChangedListener;
import gameLogic.PlayerManager;
import gameLogic.goal.Goal;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GoalController {
    private Context context;
    private Group goalButtons = new Group();

    public GoalController(Context context) {
        this.context = context;

        context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                showCurrentPlayerGoals();
            }
        });
    }

    private List<String> playerGoalStrings() {
        //Returns player goals so that they can displayed on screen
        ArrayList<String> strings = new ArrayList<String>();
        PlayerManager pm = context.getGameLogic().getPlayerManager();
        Player currentPlayer = pm.getCurrentPlayer();

        for (Goal goal : currentPlayer.getGoals()) {
            if(goal.getComplete()) {
                continue;
            }

            strings.add(goal.toString());
        }

        return strings;
    }

    public void showCurrentPlayerGoals() {
        TaxeGame game = context.getTaxeGame();
        //Displays the player's current goals
        goalButtons.remove();
        goalButtons.clear();

        PlayerManager pm = context.getGameLogic().getPlayerManager();
        Player currentPlayer = pm.getCurrentPlayer();

        float top = (float) TaxeGame.HEIGHT;
        float x = 10.0f;
        float y = top - 10.0f - TopBarController.CONTROLS_HEIGHT;

        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);
        //Draws the player's name and their score
        game.fontSmall.draw(game.batch, playerHeader(), x, y);
        y -= 30;
        //Draws "Goals:"
        game.fontSmall.draw(game.batch, "Goals:", x, y);
        game.batch.end();
        
        y -= 15;
        for (Goal goal : currentPlayer.getGoals()) {
            if (!goal.getComplete()) {
                String goalString = goal.toString();

                y -= 30;

                TextButton button = new TextButton(goalString, context.getSkin());
                GoalClickListener listener = new GoalClickListener(context, goal);

                button.setPosition(x, y);
                button.addListener(listener);
                button.getClickListener();
                goalButtons.addActor(button);
            }
        }

        
        context.getStage().addActor(goalButtons);
    }

    private String playerHeader() {
        return "Player " + context.getGameLogic().getPlayerManager().getCurrentPlayer().getPlayerNumber() + ": " + context.getGameLogic().getPlayerManager().getCurrentPlayer().getScore();
    }
}
