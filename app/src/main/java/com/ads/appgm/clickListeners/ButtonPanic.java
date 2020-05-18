package com.ads.appgm.clickListeners;

import android.view.View;
import android.widget.Toast;

public class ButtonPanic implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Botão do Pânico acionado", Toast.LENGTH_LONG).show();
    }
}
