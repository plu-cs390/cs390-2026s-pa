package fractal.core;

public interface FractalGenerationListener {
    void imageChanged();
    void imageComplete(double elapsedTime);
}
