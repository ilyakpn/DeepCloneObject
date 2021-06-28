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
        System.out.println("Source:");
        System.out.println(man.getName());
        System.out.println(man.getAge());
        System.out.println(man.getFavoriteBooks().toString());
        System.out.println();
        System.out.println("Copy:");
        System.out.println(man1.getName());
        System.out.println(man1.getAge());
        System.out.println(man1.getFavoriteBooks().toString());
    }
}
