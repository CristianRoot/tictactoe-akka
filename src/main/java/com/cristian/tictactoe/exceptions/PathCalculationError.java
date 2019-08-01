package com.cristian.tictactoe.exceptions;

import com.cristian.tictactoe.models.Path;

public class PathCalculationError extends Exception {

    private final Path path;

    public PathCalculationError(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

}
