package com.example.mathdle;

public class Choose {
    int Img;
    String Name;
    public Choose(int Img, String Name) {
        this.Name = Name;
        this.Img = Img;
    }
    public int getImg() {
        return Img;
    }
    public String getName() {
        return Name;
    }

    public void setImg(int Img) {
        this.Img = Img;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
}

