package com.shkjs.patient.data.em;

public enum RolesEm {
    USER(1L), DOCTOR(2L), ADMIN(99L), UNKNOW(0L);

    private Long mark;

    RolesEm(Long mark) {
        this.mark = mark;
    }

    public Long getMark() {
        return mark;
    }
}
