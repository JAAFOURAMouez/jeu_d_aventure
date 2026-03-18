package util;

import java.util.ArrayList;
import java.util.List;

public abstract class Sujet {
    private List<Observateur> observateurs = new ArrayList<>();

    public void attache(Observateur o) {
        if (!observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    public void detache(Observateur o) {
        observateurs.remove(o);
    }

    public void informe() {
        for (Observateur o : observateurs) {
            o.miseAJour();
        }
    }
}
