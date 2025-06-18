# FarmGame

FarmGame to prosta gra farmerska napisana w Javie przy użyciu biblioteki [libGDX](https://libgdx.com/). Projekt składa się z kilku modułów, z których najważniejsze to `core` (logika gry) oraz `lwjgl3` (wersja desktopowa).
Gra pozwala sadzic rosliny, hodowac zwierzeta oraz rozwijac bohatera dzieki zadaniom i systemowi doswiadczenia. Rozgrywke urozmaica zmienna pogoda, losowe wydarzenia i mozliwosc zapisu stanu gry.

## Wykorzystywane biblioteki

- **libGDX** – główny silnik gier wykorzystywany do renderowania i obsługi wejścia.
- **gdx-freetype** – obsługa czcionek TrueType.
- **gdx-box2d** – biblioteka do prostych obliczeń fizycznych (obecnie niewykorzystywana w kodzie gry, lecz dodana w zależnościach).


### Funkcje gry
- uprawa roslin i hodowla zwierzat
- system zadan i doswiadczenia gracza
- losowe zdarzenia oraz zmienna pogoda
- ulepszanie farmy (m.in. zagrody, system nawadniania)
- zapisywanie i wczytywanie stanu gry

Projekt zarządzany jest za pomocą Gradle i można go uruchomić komendą `./gradlew lwjgl3:run`.

## Najważniejsze klasy

### `com.farmgame.FarmGame`
Główna klasa gry rozszerzająca `Game`. Tworzy obiekt `DifficultyManager` i uruchamia `MenuScreen`.

### Pakiet `game`
Zawiera podstawową logikę rozgrywki:

- **Farm** – reprezentuje farmę składającą się z działek na rośliny (`Plot`) oraz zagród dla zwierząt (`AnimalPen`). Pozwala na odblokowywanie działek, zarządzanie pojemnością zagród i zakup systemu nawadniania.
- **Plot** – pojedyncza działka uprawna. Może być zablokowana, pusta lub zawierać roślinę. Obsługuje jej podlewanie, wzrost i zbieranie plonów.
- **Plant** oraz **PlantType** – opisują rośliny. `Plant` śledzi aktualny postęp wzrostu i czy roślina jest podlana. `PlantType` definiuje statyczne informacje jak czas wzrostu, kolor czy ceny nasion i sprzedaży.
- **Animal** oraz **AnimalType** – klasy odpowiedzialne za zwierzęta. `Animal` przechowuje stan produkcji (np. wytwarzanie mleka), a `AnimalType` definiuje jakie produkty wytwarza zwierzę, czym można je karmić oraz ile kosztuje.
- **AnimalPen** – zagroda na zwierzęta. Może być zablokowana lub posiadać określoną pojemność. Pozwala automatycznie karmić zwierzęta jeśli w magazynie znajdują się odpowiednie rośliny.
- **AnimalDatabase** i **PlantDatabase** – klasy narzędziowe zawierające z góry zdefiniowane typy roślin oraz zwierząt dostępne w grze.
- **GameClock** – prosty zegar gry dzielący czas na pory dnia i dni tygodnia.
- **Weather** – generuje losową pogodę wpływającą m.in. na szybkość wzrostu roślin.
- **MessageManager** – wyświetla komunikaty na ekranie w postaci znikających etykiet.
- **RandomEventManager** – raz na dobę losuje zdarzenie (np. burzę) mogące zniszczyć część upraw lub odebrać graczowi pieniądze.
- **DifficultyManager** – zarządza poziomem trudności wpływającym na mnożniki czasu i pieniędzy.

### Pakiet `player`

- **Player** – przechowuje informacje o graczu, jego poziomie, doświadczeniu oraz pieniądzach. Posiada magazyn przedmiotów (`Inventory`).
- **Inventory** i **InventoryItem** – prosty system magazynu umożliwiający dodawanie i usuwanie przedmiotów.

### Pakiet `quest`

- **QuestDatabase** – lista przykładowych zadań (dostarcz określone przedmioty).
- **Quest** – definicja pojedynczego zadania wraz z nagrodami.
- **QuestManager** – przechowuje aktywne zadania i umożliwia ich wczytanie z bazy danych.

### Pakiet `game_save`
Odpowiada za zapisywanie i wczytywanie stanu gry do pliku JSON.

- **SaveManager** – tworzy strukturę `GameState`, zapisuje ją do pliku i odczytuje przy wczytywaniu.
- **GameState** oraz `Saved*` – zestaw pomocniczych klas reprezentujących dane w pliku zapisu (stan farmy, gracza, roślin, zwierząt itp.).

### Pakiet `screen`

- **MenuScreen** – ekran początkowy pozwalający rozpocząć nową grę lub wczytać zapis.
- **GameScreen** – główny ekran gry odpowiedzialny za renderowanie planszy, obsługę interfejsu oraz logikę interakcji gracza.

### Pakiet `ui`
Zawiera niewielkie okna interfejsu używane na ekranie gry, m.in. wybór zwierząt, sprzedaż przedmiotów czy podgląd zadań.
W menu startowym mozesz podac nazwe farmy i wybrac poziom trudnosci (latwy, sredni lub trudny). Trudniejszy poziom oznacza wyzsze ceny odblokowania oraz wolniejszy wzrost roslin.
Gra obsluguje piec slotow zapisu. W kazdej chwili na ekranie gry mozesz zapisac stan i wczytac go pozniej.

## Uruchamianie

1. Wykonaj `./gradlew lwjgl3:run` aby uruchomić wersję desktopową. 
2. Zapisy znajdują się w katalogu z grą jako pliki `farmgame_save_*.json`. 
