package com.agile.planning_poker.userstory;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Priority {
    MUST_HAVE,
    SHOULD_HAVE,
    COULD_HAVE,
    WONT_HAVE
}
