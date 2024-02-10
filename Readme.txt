Onel Alexandru 332CB
    Am inceput tema prin intelegerea algoritmilor pe foaie,
am luat exemplul cu Round Robin din pdf si dupa ce l am inteles,
am trecut la implementarea algoritmului in MyDispatcher. Am luat
o variabila lastId in care am salvat ultimul Id al hostului. Am initializat-
o cu 0 pentru ca asa era regula. Am trimis task ul hostului si am
actualizat lastId folosind formula data. Pe urma am implementat in
clasa MyHost astfel: Am luat o variabila hostIsRunning care in functie
de valoarea sa, 0 sau 1, mentine host ul in functia run(). Aceasta isi
schimba valoarea doar la apelarea functiei shutdown(). Pe urma am folosit
wait() pentru a astepta cat timp coada este goala. Cand este adaugat un task
in coada se apeleaza notifyAll() si thread ul iese din wait(). Totodata, daca
nu se vor mai adauga task uri, am avut nevoie sa dau un notifyAll() si la shutdown
pentru a nu ramane blocat in acel wait(). Dupa ce am task uri in coada, am extras
primul task, sincronizand operatia. Pe urma verific tipul taskului, daca nu este
preemptibil am un while() in care verific daca mai am timp ramas de rulat pentru
acel task. In while() imi iau un start si end care masoara exact timpul cand am
inceput rularea si iesirea din modul de rulare, calculez diferenta si actualizez
currentTask.getLeft(). Am implementat asa, pentru a se putea adauga alte task uri
cat timp se ruleaza un task non preemptibil. Nu sunt sigur daca merge si doar
sleep de currentTask.getLeft(). Pe cazul cu task ul este preemptibil este diferit
faptul ca dau wait(currentTask.getLeft()), dar nu ma aflu intr un loop. Pe urma
dupa ce am updatat currentTask.getLeft(), verific daca este terminat task ul.
Daca este terminat setez variabilele aferent, altfel adaug inapoi in coada task ul,
pentru a alege un task mai bun daca a fost adaugat altul.
    Pentru metoda addTask(), doar am adaugat task ul in coada si am notificat.
    Pentru metoda getQueueSize() am verificat daca este ceva rulat, si daca este
am adaugat +1 altfel am trimis doar lungimea cozii.
    Pentru getWorkLeft() am parcurs toata coada si am adunat timpul de executie
ramas pentru fiecare task. Totodata daca un task rula deja am adunat timpul sau
minus cat a petrecut deja in starea running, de aceea am luat variabila
startTimeGlobal.