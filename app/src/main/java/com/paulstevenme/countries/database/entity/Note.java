package com.paulstevenme.countries.database.entity;


import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "flag")
    private String flag;

    @ColumnInfo(name = "region")
    private String region;

    @ColumnInfo(name = "population")
    private int population;



    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "capital")
    private String capital;

    @ColumnInfo(name = "currencies")
    private String currencies;

    @ColumnInfo(name = "topLevelDomain")
    private String topLevelDomain;



    @ColumnInfo(name = "languages")
    private String languages;

    @ColumnInfo(name = "callingCodes")
    private String callingCodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
    }

    public String getCallingCodes() {
        return callingCodes;
    }

    public void setCallingCodes(String callingCodes) {
        this.callingCodes = callingCodes;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }



    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTopLevelDomain() {
        return topLevelDomain;
    }

    public void setTopLevelDomain(String topLevelDomain) {
        this.topLevelDomain = topLevelDomain;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }


    public Note() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.flag);
        dest.writeString(this.region);
        dest.writeInt(this.population);
        dest.writeString(this.capital);
        dest.writeString(this.currencies);
        dest.writeString(this.callingCodes);
        dest.writeString(this.topLevelDomain);
        dest.writeString(this.languages);
    }

    protected Note(Parcel in) {
        this.uid = in.readInt();
        this.name = in.readString();
        this.flag = in.readString();
        this.region = in.readString();
        this.population = in.readInt();
        this.capital = in.readString();
        this.currencies = in.readString();
        this.callingCodes = in.readString();
        this.topLevelDomain=in.readString();
        this.languages=in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
