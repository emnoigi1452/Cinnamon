package me.stella.objects.reporter;

public interface Reporter {

    double getCompletion();

    void setCompletion(double value);

    default Object getPhase() {
        return null;
    }

}
