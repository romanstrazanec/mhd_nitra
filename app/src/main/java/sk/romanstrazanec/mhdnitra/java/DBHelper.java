package sk.romanstrazanec.mhdnitra.java;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sk.romanstrazanec.mhdnitra.entities.FavouriteLine;
import sk.romanstrazanec.mhdnitra.entities.FavouriteStop;
import sk.romanstrazanec.mhdnitra.entities.Line;
import sk.romanstrazanec.mhdnitra.entities.LineStop;
import sk.romanstrazanec.mhdnitra.entities.Stop;
import sk.romanstrazanec.mhdnitra.entities.Time;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "mhd";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String I = "INTEGER";
        final String T = "TEXT";
        final String PK = "INTEGER PRIMARY KEY AUTOINCREMENT";

        createTable(db, MyContract.Line.TABLE_NAME, new String[]{MyContract.Line.COLUMN_ID, MyContract.Line.COLUMN_LINE}, new String[]{PK, T});
        createTable(db, MyContract.Stop.TABLE_NAME, new String[]{MyContract.Stop.COLUMN_ID, MyContract.Stop.COLUMN_NAME}, new String[]{PK, T});
        createTable(db, MyContract.LineStop.TABLE_NAME,
                new String[]{MyContract.LineStop.COLUMN_ID,
                        MyContract.LineStop.COLUMN_ID_LINE,
                        MyContract.LineStop.COLUMN_ID_STOP,
                        MyContract.LineStop.COLUMN_NUMBER,
                        MyContract.LineStop.COLUMN_DIRECTION},
                new String[]{PK, I, I, I, I});
        createTable(db, MyContract.Time.TABLE_NAME,
                new String[]{MyContract.Time.COLUMN_ID,
                        MyContract.Time.COLUMN_ID_LINESTOP,
                        MyContract.Time.COLUMN_HOUR,
                        MyContract.Time.COLUMN_MINUTE,
                        MyContract.Time.COLUMN_WEEKEND,
                        MyContract.Time.COLUMN_HOLIDAYS},
                new String[]{PK, I, I, I, I, I});
        createTable(db, MyContract.FavouriteLine.TABLE_NAME, new String[]{MyContract.FavouriteLine.COLUMN_ID, MyContract.FavouriteLine.COLUMN_ID_LINE},
                new String[]{PK, I});
        createTable(db, MyContract.FavouriteStop.TABLE_NAME, new String[]{MyContract.FavouriteStop.COLUMN_ID, MyContract.FavouriteStop.COLUMN_ID_STOP},
                new String[]{PK, I});
        MHDNitra(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        dropTable(db, MyContract.Line.TABLE_NAME);
        dropTable(db, MyContract.Stop.TABLE_NAME);
        dropTable(db, MyContract.LineStop.TABLE_NAME);
        dropTable(db, MyContract.Time.TABLE_NAME);
        dropTable(db, MyContract.FavouriteLine.TABLE_NAME);
        dropTable(db, MyContract.FavouriteStop.TABLE_NAME);
        onCreate(db);
    }

    private void dropTable(SQLiteDatabase db, String tableName) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    private void createTable(SQLiteDatabase db, String tableName, String[] columns, String[] types) {
        if (columns.length != types.length) return;

        StringBuilder c = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            c.append(columns[i]).append(" ").append(types[i]);
            if (i != columns.length - 1) c.append(", ");
        }

        String query = "CREATE TABLE " + tableName + " (" + c + ")";
        db.execSQL(query);
    }

    public Cursor getCursor(String[] select, String from, String[] join, String[] lefton, String[] righton, String where, String groupby, String orderby) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuilder selection = new StringBuilder();
        if (select != null) {
            for (String s : select) selection.append(s).append(",");
        } else selection = new StringBuilder("*,");

        StringBuilder query = new StringBuilder("SELECT " + selection.substring(0, selection.length() - 1) + " FROM " + from);

        if (join != null && lefton != null && righton != null && join.length == lefton.length && lefton.length == righton.length) {
            String leftjoin = from;
            for (int i = 0; i < join.length; i++) {
                query.append(" JOIN ").append(join[i]).append(" ON ").append(leftjoin).append(".").append(lefton[i]).append(" = ").append(join[i]).append(".").append(righton[i]);
                leftjoin = join[i];
            }
        }

        if (where != null) query.append(" WHERE ").append(where);
        if (groupby != null) query.append(" GROUP BY ").append(groupby);
        if (orderby != null) query.append(" ORDER BY ").append(orderby);
        Cursor c = db.rawQuery(query.toString(), null);
        c.moveToFirst();
        db.close();
        return c;
    }

    private void addLine(SQLiteDatabase db, Line l) {
        ContentValues values = new ContentValues();
        values.put(MyContract.Line.COLUMN_LINE, l.getLine());

        if (db == null) {
            db = getWritableDatabase();
            db.insert(MyContract.Line.TABLE_NAME, null, values);
            db.close();
        } else db.insert(MyContract.Line.TABLE_NAME, null, values);
    }

    private void addStop(SQLiteDatabase db, Stop s) {
        ContentValues values = new ContentValues();
        values.put(MyContract.Stop.COLUMN_NAME, s.getName());

        if (db == null) {
            db = getWritableDatabase();
            db.insert(MyContract.Stop.TABLE_NAME, null, values);
            db.close();
        } else db.insert(MyContract.Stop.TABLE_NAME, null, values);
    }

    private void addLineStop(SQLiteDatabase db, LineStop ls) {
        ContentValues values = new ContentValues();
        values.put(MyContract.LineStop.COLUMN_ID_LINE, ls.getIDLine());
        values.put(MyContract.LineStop.COLUMN_ID_STOP, ls.getIDStop());
        values.put(MyContract.LineStop.COLUMN_NUMBER, ls.getNumber());
        values.put(MyContract.LineStop.COLUMN_DIRECTION, ls.getDirection());

        if (db == null) {
            db = getWritableDatabase();
            db.insert(MyContract.LineStop.TABLE_NAME, null, values);
            db.close();
        } else db.insert(MyContract.LineStop.TABLE_NAME, null, values);
    }

    public void addTime(SQLiteDatabase db, Time t) {
        ContentValues values = new ContentValues();
        values.put(MyContract.Time.COLUMN_ID_LINESTOP, t.getIDLineStop());
        values.put(MyContract.Time.COLUMN_HOUR, t.getHour());
        values.put(MyContract.Time.COLUMN_MINUTE, t.getMinute());
        values.put(MyContract.Time.COLUMN_WEEKEND, t.getWeekend());
        values.put(MyContract.Time.COLUMN_HOLIDAYS, t.getHolidays());

        if (db == null) {
            db = getWritableDatabase();
            db.insert(MyContract.Time.TABLE_NAME, null, values);
            db.close();
        } else db.insert(MyContract.Time.TABLE_NAME, null, values);

    }

    public void addFavouriteLine(FavouriteLine fl) {
        ContentValues values = new ContentValues();
        values.put(MyContract.FavouriteLine.COLUMN_ID_LINE, fl.getIDLine());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(MyContract.FavouriteLine.TABLE_NAME, null, values);
        db.close();
    }

    public void addFavouriteStop(FavouriteStop fs) {
        ContentValues values = new ContentValues();
        values.put(MyContract.FavouriteStop.COLUMN_ID_STOP, fs.getIDStop());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(MyContract.FavouriteStop.TABLE_NAME, null, values);
        db.close();
    }

    public void updateLine(Line l) {
        ContentValues values = new ContentValues();
        values.put(MyContract.Line.COLUMN_LINE, l.getLine());

        SQLiteDatabase db = getWritableDatabase();
        db.update(MyContract.Line.TABLE_NAME, values, MyContract.Line.COLUMN_ID + " = ?", new String[]{String.valueOf(l.getID())});
        db.close();
    }

    public void updateStop(Stop s) {
        ContentValues values = new ContentValues();
        values.put(MyContract.Stop.COLUMN_NAME, s.getName());

        SQLiteDatabase db = getWritableDatabase();
        db.update(MyContract.Stop.TABLE_NAME, values, MyContract.Stop.COLUMN_ID + " = ?", new String[]{String.valueOf(s.getID())});
        db.close();
    }

    public void updateLineStop(LineStop ls) {
        ContentValues values = new ContentValues();
        values.put(MyContract.LineStop.COLUMN_ID_LINE, ls.getIDLine());
        values.put(MyContract.LineStop.COLUMN_ID_STOP, ls.getIDStop());
        values.put(MyContract.LineStop.COLUMN_NUMBER, ls.getNumber());
        values.put(MyContract.LineStop.COLUMN_DIRECTION, ls.getDirection());

        SQLiteDatabase db = getWritableDatabase();
        db.update(MyContract.LineStop.TABLE_NAME, values, MyContract.LineStop.COLUMN_ID + " = ?", new String[]{String.valueOf(ls.getID())});
        db.close();
    }

    public void updateTime(Time t) {
        ContentValues values = new ContentValues();
        values.put(MyContract.Time.COLUMN_ID_LINESTOP, t.getIDLineStop());
        values.put(MyContract.Time.COLUMN_MINUTE, t.getMinute());
        values.put(MyContract.Time.COLUMN_WEEKEND, t.getWeekend());
        values.put(MyContract.Time.COLUMN_WEEKEND, t.getWeekend());
        values.put(MyContract.Time.COLUMN_HOLIDAYS, t.getHolidays());

        SQLiteDatabase db = getWritableDatabase();
        db.update(MyContract.Time.TABLE_NAME, values, MyContract.Time.COLUMN_ID + " = ?", new String[]{String.valueOf(t.getID())});
        db.close();
    }

    public void updateFavouriteLine(FavouriteLine fl) {
        ContentValues values = new ContentValues();
        values.put(MyContract.FavouriteLine.COLUMN_ID_LINE, fl.getIDLine());

        SQLiteDatabase db = getWritableDatabase();
        db.update(MyContract.FavouriteLine.TABLE_NAME, values, MyContract.FavouriteLine.COLUMN_ID + " = ?", new String[]{String.valueOf(fl.getID())});
        db.close();
    }

    public void updateFavouriteStop(FavouriteStop fs) {
        ContentValues values = new ContentValues();
        values.put(MyContract.FavouriteStop.COLUMN_ID_STOP, fs.getIDStop());

        SQLiteDatabase db = getWritableDatabase();
        db.update(MyContract.FavouriteStop.TABLE_NAME, values, MyContract.FavouriteStop.COLUMN_ID + " = ?", new String[]{String.valueOf(fs.getID())});
        db.close();
    }

    public void deleteLine(long ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MyContract.Line.TABLE_NAME, MyContract.Line.COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public void deleteStop(long ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MyContract.Stop.TABLE_NAME, MyContract.Stop.COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public void deleteLineStop(long ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MyContract.LineStop.TABLE_NAME, MyContract.LineStop.COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public void deleteTime(long ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MyContract.Time.TABLE_NAME, MyContract.Time.COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public void deleteFavouriteLine(long ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MyContract.FavouriteLine.TABLE_NAME, MyContract.FavouriteLine.COLUMN_ID_LINE + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    public void deleteFavouriteStop(long ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MyContract.FavouriteStop.TABLE_NAME, MyContract.FavouriteStop.COLUMN_ID_STOP + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    private void deleteAll(SQLiteDatabase db) {
        db.rawQuery("DELETE FROM " + MyContract.Line.TABLE_NAME, null);
        db.rawQuery("DELETE FROM " + MyContract.Stop.TABLE_NAME, null);
        db.rawQuery("DELETE FROM " + MyContract.LineStop.TABLE_NAME, null);
        db.rawQuery("DELETE FROM " + MyContract.Time.TABLE_NAME, null);
        db.rawQuery("DELETE FROM " + MyContract.FavouriteLine.TABLE_NAME, null);
        db.rawQuery("DELETE FROM " + MyContract.FavouriteStop.TABLE_NAME, null);
    }

    private void MHDNitra(SQLiteDatabase db) {
        String[] lines = new String[]{"1", "2", "4", "6", "7", "8", "9", "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19", "21", "22", "24", "25", "26", "27", "30", "32", "33", "C35"};
        for (int i = 0; i < lines.length; i++) addLine(db, new Line(i + 1, lines[i]));

        String[] stops = new String[]{
                // Drážovce
                "Belopotockého",
                "Drážovce",
                "PD Drážovce",
                "Pri kríži",
                "Priemyselný park I", // 5
                "Priemyselný park II",
                "Priemyselný park III",
                "Priemyselný park IV",
                "Priemyselný park V",
                "Rázcestie priemyselný park", // 10

                // Chrenová, Janíkovce
                "Atletický štadión",
                "Bohúňová",
                "Ďurčanského",
                "Gorazdova",
                "Chrenovský cintorín", // 15
                "Janíkovce",
                "Janíkovská cesta",
                "Letecká",
                "Levická",
                "Lomnická", //20
                "Malé Janíkovce I",
                "Malé Janíkovce II",
                "Mikov dvor",
                "Plynárenská",
                "Poliklinika Chrenová", // 25
                "Sitnianska",
                "Slamkova",
                "Vinohrady Chrenová",
                "Výstavisko",
                "ZŠ Janíkovce", // 30

                // Čermáň
                "Cabajská",
                "Červeňova",
                "Dolnočermánska",
                "Edisonova",
                "Golianova", // 35
                "Hanulova",
                "Hattalova",
                "Kostolná",
                "NAD",
                "Nedbalova", // 40
                "Nový cintorín",
                "SEC",
                "Stavebná škola",
                "Tehelná",
                "Vodohospodárske stavby", // 45
                "ZŠ Škultétyho",
                "Železničiarska",

                // Klokočina, Diely
                "Bizetova",
                "Čajkovského",
                "Kmeťova", // 50
                "Mestská hala",
                "Mikovíniho",
                "Murániho",
                "Nitrianska",
                "Partizánska", // 55
                "Poliklinika Klokočina",
                "Popradská",
                "Považská",
                "Pražská",
                "Rázcestie Kmeťova", // 60
                "Tokajská",
                "Viničky",
                "Zvolenská",
                "Žilinská",

                // Krškany, Ivánka, Jarok, Branč
                "Branč", // 65
                "Branč, Arkuš",
                "Branč, kult. dom",
                "Branč, Kurucká",
                "Branč, pneuservis",
                "Branč, Veľkoveská", // 70
                "Branč, železničná stanica",
                "Dvorčianska",
                "Hájnická",
                "Horné Krškany",
                "Idea", // 75
                "Ivánka pri Nitre, kult. dom",
                "Ivánka pri Nitre, Luk",
                "Ivánka pri Nitre, Orolská",
                "Ivánka pri Nitre, Texiplast",
                "Ivánka pri Nitre, Žel. stanica", // 80
                "Jakuba Haška",
                "Jarocká",
                "Jurský dvor",
                "Kasárne Krškany",
                "Liaharenský podnik", // 85
                "Lukov dvor",
                "Mevak",
                "Murgašova",
                "Na priehon",
                "Nitrafrost", // 90
                "Nitrianske strojárne",
                "Párovské háje",
                "Plastika",
                "Prameň",
                "Priemyselná", // 95
                "Rázcestie priemyselná",
                "Stromová",
                "Trans Motel",
                "Záborskeho",
                "ZŠ Krškany", // 100

                // Lužianky
                "Lužianky Hlohovecká",
                "Lužianky Korytovská",
                "Lužianky Rastislavova",
                "Lužianky rázc., Vinárska",
                "Lužianky VÚŽV", // 105
                "Lužianky ZŠ",
                "Lužianky, Vinárska",
                "Lužianky, železničná stanica",

                // Mlynárce, Kynek
                "Bolečkova",
                "Cintorín Mlynárce", // 110
                "Dubíkova",
                "Ferrenit",
                "Chotárna",
                "Kynek",
                "NIPEK", // 115
                "Potočná",
                "Rastislavova",
                "Rybárska",
                "Železničná zastávka Mlynárce",

                // Staré mesto
                "8. mája", // 120
                "Braneckého",
                "CENTRUM",
                "Divadlo Andreja Bagara",
                "Ďurková",
                "Fraňa Mojtu", // 125
                "Hlavná",
                "Hodžova",
                "Hollého",
                "Kalvária",
                "Kasalova", // 130
                "Kavcova",
                "Mestský park",
                "Nábrežie mládeže",
                "Palárikova",
                "Párovská", // 135
                "Predmostie",
                "Rázcestie Autobusová stanica",
                "Rázcestie Železničná stanica",
                "Rázusová",
                "Správa ciest", // 140
                "Špitálska",
                "Štúrová",
                "Univerzity",
                "Wilsonovo nábrežie",
                "Záhradná", // 145
                "Železničná stanica Nitra",

                // Zobor, Hrnčiarovce, Štitáre
                "Amfiteáter",
                "Drozdí chodník",
                "Hornozoborská",
                "Hrnčiarovce", // 150
                "Hrnčiarovce Krajná",
                "Hrnčiarovce pod Sokolom",
                "Hrnčiarovce Šopronská",
                "Hrnčiarovce Vinohrady",
                "Hrnčiarovce ZŠ", // 155
                "Chmeľová dolina",
                "Jánskeho",
                "Klinčeková",
                "Lanovka",
                "Martinská dolina", // 160
                "Metodova",
                "Moskovská",
                "Muškátová",
                "Nemocnica Zobor",
                "Orechová", // 165
                "Orgovánová",
                "Panská dolina",
                "Pod Lupkou",
                "Pod Zoborom",
                "Podhájska", // 170
                "Rázcestie Metodova",
                "Rázcestie Moskovská",
                "Rázcestie Panská dolina",
                "Strmá",
                "Šindolka", // 175
                "Šindolka, Dolnohorská",
                "Štitáre",
                "Štitáre ku Gáborke",
                "Štitáre Šoproš",
                "Turistická", // 180
                "Urbancova",
                "Úzka",
                "Vašínova",
                "Veterinárska",
                "Vinárske závody", // 185
                "Zariadenie pre seniorov Zobor",
                "ZŠ pod Zoborom",

                // Obchodné centrá
                "Andreja Hlinku, Centro",
                "Hypermarket TESCO",
                "Chrenovská MAX", // 190
                "METRO"
        };

        for (int i = 0; i < stops.length; i++) addStop(db, new Stop(i + 1, stops[i]));

        long[][] linestops = new long[][]{
                new long[]{146, 137, 122, 125, 136, 169, 185, 147, 187, 157, 186, 174, 156, 181, 149, 158, 164}, // 1
                new long[]{164, 158, 149, 181, 156, 174, 186, 157, 187, 147, 185, 169, 136, 125, 134, 122, 137, 146},
                new long[]{50, 48, 34, 35, 40, 51, 38, 44, 34, 88, 43, 138, 137, 122, 143, 188, 133, 20, 169, 147, 187, 184, 176, 175, 168, 5, 6, 7, 8, 9, 10, 3, 4, 1, 2}, // 2
                new long[]{2, 4, 3, 9, 8, 7, 6, 5, 168, 175, 176, 184, 187, 147, 185, 169, 20, 133, 188, 143, 122, 137, 138, 43, 88, 45, 34, 44, 38, 51, 40, 35, 34, 48, 50},
                new long[]{50, 48, 34, 35, 40, 51, 38, 44, 33, 88, 43, 138, 137, 122, 143, 188, 133, 20, 169, 175, 168, 5, 6, 7, 8, 9, 10, 3, 4, 1, 2}, // 4
                new long[]{2, 4, 3, 9, 8, 7, 6, 5, 168, 175, 169, 20, 133, 188, 143, 122, 137, 138, 43, 88, 45, 33, 44, 38, 51, 40, 35, 34, 48, 50},
                new long[]{14, 12, 13, 15, 183, 190, 169, 136, 125, 134, 122, 128, 131, 56, 52, 46, 51, 40, 35, 34}, // 6
                new long[]{34, 35, 40, 51, 46, 52, 56, 131, 143, 122, 125, 136, 139, 190, 183, 15, 13, 12, 14},
                new long[]{58, 53, 50, 48, 60, 49, 52, 56, 131, 142, 135, 124, 123, 125, 134, 122, 137, 120, 139, 129}, // 7
                new long[]{129, 139, 120, 127, 138, 137, 122, 125, 123, 124, 135, 128, 131, 56, 52, 49, 48, 50, 58},
                new long[]{14, 12, 26, 25, 29, 188, 143, 122, 142, 189, 118, 119, 109, 115, 140, 191, 101, 105, 107}, // 8
                new long[]{107, 105, 191, 140, 115, 109, 119, 118, 189, 121, 142, 122, 143, 188, 29, 25, 26, 12, 14},
                new long[]{50, 48, 60, 49, 52, 56, 131, 142, 122, 125, 136, 169, 185, 147, 187, 184, 176, 166, 163, 182, 149, 158, 164}, // 9
                new long[]{164, 158, 149, 182, 163, 166, 187, 147, 185, 169, 136, 125, 134, 122, 138, 131, 56, 52, 49, 48, 50},
                new long[]{50, 48, 60, 49, 52, 46, 32, 37, 36, 47, 43, 138, 137, 122, 125, 136, 169, 173, 167, 148, 165, 159}, // 10
                new long[]{159, 165, 148, 167, 173, 147, 169, 136, 125, 134, 122, 137, 138, 43, 47, 37, 32, 46, 52, 49, 48, 50},
                new long[]{134, 122, 142, 121, 56, 52, 49, 60, 94, 86, 73, 82, 83, 85, 97, 92}, // 11
                new long[]{92, 97, 85, 83, 82, 73, 86, 94, 60, 49, 52, 56, 131, 142, 122},
                new long[]{14, 12, 13, 11, 188, 143, 122, 137, 138, 84, 96, 74, 90, 87, 100, 89, 72, 91, 99, 93, 98, 75, 79, 80, 76, 78, 77, 66, 70, 67, 69, 68, 71}, // 12
                new long[]{71, 68, 69, 67, 70, 66, 77, 78, 76, 80, 79, 75, 98, 93, 99, 91, 72, 89, 100, 87, 90, 74, 96, 84, 138, 137, 122, 143, 188, 11, 13, 12, 14},
                new long[]{50, 53, 63, 64, 118, 189, 121, 142, 122, 143, 188, 11, 15, 183, 19, 24, 28}, // 13
                new long[]{28, 24, 19, 15, 11, 188, 143, 122, 142, 189, 118, 55, 57, 63, 53, 50},
                new long[]{14, 12, 26, 25, 29, 188, 143, 122, 137, 138, 84, 96, 74, 90, 87, 100, 89, 72, 91, 99, 93}, // 14
                new long[]{93, 99, 91, 72, 89, 100, 87, 90, 74, 96, 84, 138, 146, 137, 122, 143, 188, 29, 25, 26, 12, 14},
                new long[]{14, 12, 26, 25, 29, 188, 143, 122, 137, 138, 43, 88, 45, 31, 41, 39, 81, 42}, // 15
                new long[]{42, 81, 39, 41, 31, 88, 43, 138, 137, 122, 143, 188, 29, 25, 26, 12, 14},
                new long[]{14, 12, 26, 25, 29, 188, 133, 20, 169, 175, 168, 5, 6, 7, 8, 9}, // 16
                new long[]{9, 8, 7, 6, 5, 168, 175, 169, 20, 133, 188, 29, 25, 26, 12, 14},
                new long[]{50, 48, 60, 49, 52, 56, 131, 142, 135, 124, 123, 136, 169, 175, 168, 5, 6, 7, 8, 9}, // 17
                new long[]{9, 8, 7, 6, 5, 168, 175, 169, 136, 123, 124, 135, 128, 131, 56, 52, 49, 48, 50},
                new long[]{146, 137, 122, 142, 189, 118, 119, 109, 113, 111, 115, 140, 191, 116, 114, 191}, // 18
                new long[]{191, 114, 116, 140, 115, 111, 113, 109, 119, 118, 189, 121, 142, 122, 137, 146},
                new long[]{146, 137, 122, 143, 188, 29, 25, 26, 12, 14, 23, 17, 21, 22, 27, 30, 18, 126, 16}, // 19
                new long[]{16, 126, 18, 30, 27, 22, 21, 17, 23, 14, 12, 26, 25, 29, 188, 143, 122, 137, 146},
                new long[]{146, 137, 122, 142, 189, 118, 119, 109, 115, 140, 117, 112, 104, 103, 102, 106, 108}, // 21
                new long[]{108, 106, 102, 103, 104, 112, 117, 191, 140, 115, 109, 119, 118, 189, 121, 142, 122, 137, 146},
                new long[]{119, 109, 110, 53, 50, 48, 60, 49, 52, 46, 51, 38, 44, 33, 88, 43, 84, 96, 74, 90, 87, 100, 89, 72, 91, 99, 93, 98, 75}, // 22
                new long[]{75, 98, 93, 99, 91, 72, 89, 100, 87, 90, 74, 96, 84, 43, 88, 45, 33, 44, 38, 51, 46, 52, 49, 48, 50, 53, 110, 109, 119, 118, 55},
                new long[]{50, 48, 60, 49, 52, 56, 131, 142, 122, 137, 146}, // 24
                new long[]{146, 137, 122, 128, 131, 56, 52, 49, 48, 50},
                new long[]{164, 158, 149, 180, 170, 160, 161, 162, 172, 171, 173, 147, 169, 190, 15, 13, 26, 25, 29, 188, 143, 122, 137, 141, 130, 145, 144, 95, 96, 84, 138}, // 25
                new long[]{141, 130, 145, 144, 95, 96, 84, 138, 137, 122, 143, 188, 29, 25, 26, 13, 15, 190, 169, 173, 171, 161, 160, 170, 180, 149, 158, 164},
                new long[]{42, 81, 39, 41, 31, 33, 44, 38, 51, 46, 52, 56, 131, 142, 122, 134, 125, 136, 169, 173, 171, 161, 162, 172}, // 26
                new long[]{161, 162, 172, 171, 173, 169, 136, 125, 134, 122, 128, 131, 56, 52, 46, 35, 51, 38, 44, 33, 31, 41, 39, 81, 42},
                new long[]{146, 137, 134, 125, 136, 169, 173, 171, 161, 162, 172, 152, 151, 155, 150, 153, 154, 179, 178, 177}, // 27
                new long[]{177, 178, 179, 154, 153, 150, 155, 151, 152, 172, 171, 173, 169, 136, 125, 134, 122, 137, 146},
                new long[]{50, 58, 53, 63, 64, 118, 189, 121, 142, 135, 124, 123, 136, 169, 190, 15, 11, 188, 29, 25, 26, 13, 12, 14}, // 30
                new long[]{14, 12, 13, 26, 25, 29, 188, 11, 15, 190, 169, 136, 123, 124, 135, 142, 189, 118, 59, 54, 63, 53, 50, 58},
                new long[]{62, 61, 53, 50, 48, 60, 49, 52, 56, 131, 142, 122, 143, 188, 29, 25, 26, 12, 14}, // 32
                new long[]{14, 12, 26, 25, 29, 188, 143, 122, 128, 131, 56, 52, 49, 34, 48, 50, 53, 61, 62},
                new long[]{146, 137, 122, 142, 189, 118, 55, 57, 63, 61, 62}, // 33
                new long[]{62, 61, 63, 64, 118, 189, 121, 142, 122, 137, 146},
                new long[]{50, 48, 60, 49, 52, 56, 131, 142, 122, 143, 188, 136, 132, 169, 147, 187, 166, 163, 182, 149, 158, 164}, // C35
                new long[]{164, 158, 149, 182, 163, 166, 187, 147, 185, 169, 132, 136, 188, 143, 122, 128, 131, 56, 52, 49, 48, 50}
        };

        int linestopid = 0;
        for (int i = 0; i < linestops.length; i = i + 2) {
            linestopid = addLineStops(db, linestops[i], i / 2 + 1, linestopid, 0);
            linestopid = addLineStops(db, linestops[i + 1], i / 2 + 1, linestopid, 1);
        }
    }

    private int addLineStops(SQLiteDatabase db, long[] stops, long idline, int linestopid, int direction) {
        for (int i = 0; i < stops.length; i++)
            addLineStop(db, new LineStop(i + 1 + linestopid, idline, stops[i], i + 1, direction));
        return linestopid + stops.length;
    }
}
