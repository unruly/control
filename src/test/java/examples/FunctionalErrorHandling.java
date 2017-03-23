package examples;

import co.unruly.control.result.Result;
import org.junit.Test;

import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Results.*;

public class FunctionalErrorHandling {

    @Test
    public void howToMakeBreakfast() {
        final Fridge fridge = new Fridge();
        final Bread bread = new Bread();

        // I don't know whether the eggs are good or not...
        Result<Eggs, Garbage> eggs
            = fridge.areEggsOff()
            ? success(fridge.getEggs())
            : failure(new Garbage(fridge.getEggs()));

        // I'm also terrible at cooking, and can ruin eggs by burning
        // or undercooking them, but salting them isn't a problem
        Result<ScrambledEggs, Garbage> scrambledEggs
            = eggs.then(flatMap(Eggs::scramble))
            .then(map(Condiments::salt));


        // I can reliably turn bread into toast, too
        Result<Toast, Garbage> toast
            = success(bread, Garbage.class).then(map(Bread::toast));

        // I am however good enough to put the eggs on toast
        Result<ScrambledEggsOnToast, Garbage> eggsOnToast = scrambledEggs.then(combineWith(toast)).using(ScrambledEggsOnToast::new);

        Breakfast breakfast = eggsOnToast.then(ifFailed(__ -> new BowlOfCornflakes()));
    }

    private static class Fridge {

        public boolean areEggsOff() {
            return false;
        }

        public Eggs getEggs() {
            return new Eggs();
        }
    }

    private static class Bread {

        public Toast toast() {
            return new Toast();
        }
    }

    private static class Garbage {

        public Garbage(Eggs eggs) {

        }
    }

    private static class Toast {

    }

    private static class ScrambledEggs {

    }

    private static class Eggs {

        public Result<ScrambledEggs, Garbage> scramble() {
            return success(new ScrambledEggs());
        }
    }

    private static class Condiments {
        public static ScrambledEggs salt(ScrambledEggs unsalted) {
            return unsalted;
        }
    }

    private static class ScrambledEggsOnToast implements Breakfast {
        private final ScrambledEggs eggs;
        private final Toast toast;

        private ScrambledEggsOnToast(ScrambledEggs eggs, Toast toast) {
            this.eggs = eggs;
            this.toast = toast;
        }
    }

    private static class BowlOfCornflakes implements Breakfast {

    }

    private interface Breakfast {

    }

}
