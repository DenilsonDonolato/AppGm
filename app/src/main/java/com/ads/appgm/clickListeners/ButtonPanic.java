package com.ads.appgm.clickListeners;

import android.view.View;
import android.widget.Toast;

import com.ads.appgm.notification.Notification;

public class ButtonPanic implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Botão do Pânico acionado", Toast.LENGTH_LONG).show();
        Notification notification = new Notification(v.getContext());
        notification.show("Notificação 1", "Notificação teste", 1);
        notification.show("Notificação 2", "Notificação teste 2", 2);
    }
}
