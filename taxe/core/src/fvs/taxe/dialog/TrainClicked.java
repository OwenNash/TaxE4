package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.resource.Train;
//Responsible for checking whether the train is clicked.
public class TrainClicked extends ClickListener {
    private Context context;
    private Train train;

    public TrainClicked(Context context, Train train) {
        this.train = train;
        this.context = context;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (Game.getInstance().getState() != GameState.NORMAL) return;

        // current player can't be passed in as it changes so find out current player at this instant
        Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

        if (!train.isOwnedBy(currentPlayer)) {
            return;
        }

        if (train.getFinalDestination() == null) {
        }
        else {
        }
        DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, train);
        DialogResourceTrain dia = new DialogResourceTrain(train, context.getSkin(), train.getPosition() != null);
        dia.show(context.getStage());
        dia.subscribeClick(listener);
    }



}
