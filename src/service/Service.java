package service;

public final class Service {
    private static Integer counterTaskID = 1;

    private Service() {
    }

    public static Integer generatedID() {
        return counterTaskID++;
    }


}
