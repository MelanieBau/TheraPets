package com.example.therapets;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;


// Inicializar Cloudinary (servicio de subida de imágenes)
public class TherapetsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Configuración de credenciales de Cloudinary para poder subir fotos
        Map config = new HashMap();
        config.put("cloud_name", "dre2owild");
        config.put("api_key", "217945322553883");
        config.put("api_secret", "dTifUy8AUeiXvHwbiLDU8uyk2Qs");

        // Iniciar Cloudinary para que esté disponible en toda la app
        MediaManager.init(this, config);
    }
}