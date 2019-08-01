package com.cristian.tictactoe.exceptions;

import com.cristian.tictactoe.models.Path;

public class PathCalculationErrorException extends Exception {

    private final Path path;

    public PathCalculationErrorException(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

}
