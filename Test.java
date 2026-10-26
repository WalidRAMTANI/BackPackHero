
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        // Données de test
        List<Integer> list = new ArrayList<>(
            Arrays.asList(5, 2, 8, 2, 10, 5)
        );

        // Afficher les résultats
        System.out.println("Liste : " + list);
        System.out.println("Taille : " + list.size());

        int max = list.stream()
                      .max(Integer::compareTo)
                      .orElseThrow();

        System.out.println("Maximum : " + max);
    }
}
