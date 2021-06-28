import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("book1");
        list.add("book2");
        list.add("book3");

        Man man = new Man("John", 25, list);

        Man man1 = CopyUtils.deepCopy(man);

        man.setName("Vasia");
        man.setAge(78);

        man1.setName("DEEP COPY!!!");

        man1.getFavoriteBooks().add("new book");
    }
}
