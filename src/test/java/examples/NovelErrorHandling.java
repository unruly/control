package examples;

import co.unruly.control.result.Result;

import static co.unruly.control.result.Resolvers.collapse;
import static co.unruly.control.result.Result.failure;
import static co.unruly.control.result.Result.success;
import static co.unruly.control.result.Transformers.attempt;
import static co.unruly.control.result.Transformers.onSuccess;
import static java.lang.String.format;

public class NovelErrorHandling {

    public static String novelSales(Author author, Publisher publisher, Editor editor, Retailer retailer) {
        final Result<Idea, String> maybeIdea = success(new Idea("Harry Potter and the Sorcerer's Cheeseburger", 12));

        return maybeIdea
                .then(attempt(publisher::getAdvance))
                .then(attempt(author::writeNovel))
                .then(onSuccess(editor::editNovel))
                .then(onSuccess(publisher::publishNovel))
                .then(onSuccess(retailer::sellNovel))
                .then(onSuccess(sales -> format("%s sold %d copies", sales.novel, sales.copiesSold)))
                .then(collapse());
    }

    public static class Author {
        private final int skill;
        private final int lifestyleCosts;

        public Author(Result<Idea, String> idea, int skill, int lifestyleCosts) {
            this.skill = skill;
            this.lifestyleCosts = lifestyleCosts;
        }

        public Result<Manuscript, String> writeNovel(Advance advance) {
            if(advance.amount > lifestyleCosts) {
                int happiness = advance.amount - lifestyleCosts;
                return success(new Manuscript(advance.idea.title, happiness * skill));
            } else {
                return failure("Ran out of money, went back to work at Tescos");
            }
        }
    }

    public static class Publisher {

        public final int qualityThreshold;
        public final int generosity;

        public Publisher(int qualityThreshold, int generosity) {
            this.qualityThreshold = qualityThreshold;
            this.generosity = generosity;
        }

        public Result<Advance, String> getAdvance(Idea idea) {
            if(idea.appeal >= qualityThreshold) {
                return success(new Advance(idea.appeal * generosity, idea));
            } else {
                return failure("This novel wouldn't sell");
            }
        }

        public Novel publishNovel(Manuscript manuscript) {
            return new Novel(manuscript.title, manuscript.quality);
        }
    }

    public static class Editor {
        public Manuscript editNovel(Manuscript manuscript) {
            return new Manuscript(manuscript.title, manuscript.quality + 3);
        }
    }

    public static class Retailer {
        private final int customerCount;

        public Retailer(int customerCount) {
            this.customerCount = customerCount;
        }

        public Sales sellNovel(Novel novel) {
            return new Sales(novel, novel.quality * customerCount);
        }
    }

    public static class Idea {
        public final String title;
        public final int appeal;

        public Idea(String title, int appeal) {
            this.title = title;
            this.appeal = appeal;
        }
    }

    public static class Advance {
        public final int amount;
        public final Idea idea;

        public Advance(int amount, Idea idea) {
            this.amount = amount;
            this.idea = idea;
        }
    }

    public static class Manuscript {
        public final String title;
        public final int quality;

        public Manuscript(String title, int quality) {
            this.title = title;
            this.quality = quality;
        }
    }

    public static class Novel {
        public final String title;
        public final int quality;

        public Novel(String title, int quality) {
            this.title = title;
            this.quality = quality;
        }
    }

    public static class Sales {
        private final Novel novel;
        private final int copiesSold;

        public Sales(Novel novel, int copiesSold) {

            this.novel = novel;
            this.copiesSold = copiesSold;
        }
    }
}
