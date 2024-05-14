package br.com.sysmap.bootcamp.domain.enums;

public enum WeekDayPoints {
        SUNDAY(25),
        MONDAY(7),
        TUESDAY(6),
        WEDNESDAY(2),
        THURSDAY(10),
        FRIDAY(15),
        SATURDAY(20);

        private final int points;

        WeekDayPoints(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }
}
