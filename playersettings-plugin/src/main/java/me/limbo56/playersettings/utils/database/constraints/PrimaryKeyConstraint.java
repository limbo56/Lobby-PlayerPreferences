package me.limbo56.playersettings.utils.database.constraints;

public class PrimaryKeyConstraint implements TableConstraint {

    private final String[] columnNames;

    public PrimaryKeyConstraint(String... columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String toString() {
        return String.format("PRIMARY KEY (%s)", String.join(", ", columnNames));
    }
}
