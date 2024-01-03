package service;

public final class Service {
    private static Integer counterTaskID = 0;

    private Service() {
    }

    public static Integer generatedID() {
        return counterTaskID++;
    }


}
