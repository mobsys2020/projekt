package com.health.myhealthapplication;

public enum enum_dosierung {
    //this char is sadly not avaible asenum key <.<
    //#("Messlöffel"),
    //workaround because enum keys cant be numbers
    z0("Messbecher"),
    z1("Stück"),
    z2("Pkg."),
    z3("Flasche"),
    z4("Beutel"),
    z5("Hub"),
    z6("Tropfen"),
    z7("Teelöffel"),
    z8("Esslöffel"),
    z9("E"),

    a("Tasse"),
    b("Applikatorfüllung"),
    c("Augenbadewanne"),
    d("Dosierbriefchen"),
    e("Dosierpipette"),
    f("Dosierspritze"),
    g("Einzeldosis"),
    h("Glas"),
    i("Likörglas"),
    j("Messkappe"),
    k("Messschale"),
    l("Mio E"),
    m("Mio IE"),
    n("Pipettenteilstrich"),
    o("Sprühstoß"),
    p("IE"),
    q("cm"),
    r("l"),
    s("ml"),
    t("g"),
    u("kg"),
    v("mg");

    private String name;

    enum_dosierung(String name) {
        this.name = name;
    }

    public String get_name() {
        return this.name;
    }
}
