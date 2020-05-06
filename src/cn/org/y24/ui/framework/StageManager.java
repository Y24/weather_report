/*
 * Copyright (c) 2019.
 *  Author: Y24
 *  All rights reserved.
 */

package cn.org.y24.ui.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class StageManager extends BaseManager<SceneManager> {
    private final LinkedList<SceneManager> currentSceneManagers = new LinkedList<>();
    private final ArrayList<Deliverer> messageBox = new ArrayList<>();
    public void sendBroadcastMessage(int senderID,Object message){
        messageBox.add(new Deliverer(senderID, Deliverer.broadcastFlag,message));
    }
    public Collection<Deliverer> receiveBroadcastMessage() {
        Collection<Deliverer> delivererCollection = new ArrayList<>();
        for (Deliverer message : messageBox) {
            if (message.getReceiverHahCode() == Deliverer.broadcastFlag) {
                delivererCollection.add(message);
            }
        }
        return delivererCollection;
    }
    public boolean showAdditional(String sceneManagerName) {
        if (get(sceneManagerName) != null) {
            currentSceneManagers.add(get(sceneManagerName));
            get(sceneManagerName).getOwnerStage().show();
            return true;
        }
        System.err.println("cannot show the Stage associated with sceneManager named " + sceneManagerName + ".");
        return false;
    }

    public boolean closeNewest() {
        if (!currentSceneManagers.isEmpty()) {
            currentSceneManagers.removeLast().getOwnerStage().close();
            return true;
        } else {
            System.err.println("No newest Stage can be selected.");
            return false;
        }

    }

    public void convertTo(String sceneManagerName) {
        closeNewest();
        showAdditional(sceneManagerName);
    }

    public void showOnly(String sceneManagerName) {
        closeAll();
        showAdditional(sceneManagerName);

    }

    public void closeAll() {
        for (SceneManager each : currentSceneManagers) {
            each.getOwnerStage().close();
        }
        //Note: after all Stages have been closed,the program tends to die,so the following code is apparently unreachable.
        currentSceneManagers.clear();
    }
}
