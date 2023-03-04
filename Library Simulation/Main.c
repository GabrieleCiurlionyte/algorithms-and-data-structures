/*  Gabriele Ciurlionyte
    11. Biblioteka be eilių (ADT: vektorius, dvejatainis medis)
    Sąlyga: Procesas: skaitytojas ateina į biblioteką ir paprašo pageidaujamos knygos,
    bibliotekos darbuotojas randa pageidaujamą knygą ar informuoja skaitytoją,
    kad tokios bibliotekoje nėra. Paieškos trukmė priklauso nuo saugyklos organizavimo būdo.
    Biblioteka laikosi principo, kad skaitytojai neturi laukti eilėse,
    todėl priima tiek darbuotojų, kiek reikia.
    Tikslas: patyrinėti: 1) kiek darbuotojų galima sutaupyti,
    jei vietoje nuoseklios paieškos nesutvarkytoje saugykloje būtų naudojama:
    a) nuosekli paieška sutvarkytoje saugykloje,
    b) dvejetainė paieška;
    2) kaip šie skaičiai kinta, augant bibliotekos fondams.
    Pradiniai duomenys: skaitytojo atvykimo tikimybė
    (galima realizacija: generuojamas atsitiktinis skaičius ir tikrinama, ar jis tenkina tam tikrą sąlygą);
    tikimybė, kad skaitytojo pageidaujama knyga bus bibliotekoje; bibliotekos knygos.
    Pastabos: pageidaujamos knygos sugeneruojamos atsitiktinai pagal pradinius duomenis;
    nuoseklios paieškos atvejais knygos saugomos vektoriuje
    ir paieškos trukmė yra lygi ieškant atliktų palyginimų skaičiui;
    dvejetainės paieškos atveju knygos saugomos dvejetainiame paieškos medyje
    ir paieškos trukmė yra lygi teoriškai įvertintam operacijų skaičiui. */


#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include "stdbool.h"
#include "cvector.h"
#include "cvector_utils.h"
#include "Binary_search_tree.h"

struct client{
    int bookID;
    int time;
};


//FUNCTION DECLARATIONS
int GenerateProbability();
int ReturnWantedBookID(int amountOfBooks, cvector_vector_type(int )vect_sorted);
int ReturnBookIndex(cvector_vector_type(int )vect, int amountOfBooks, int wantedBookID);

//Selection sort
void Selection_Sort(cvector_vector_type(int ) vect_sorted, int n);
void swap(int* xp, int* yp);

//BST
struct Node* sortedVectorToBST(cvector_vector_type(int ) vect_sorted, int amount_of_books);
//Searching methods
int searchInUnsortedDatabase(int amount_of_books, int wanted_book_ID, cvector_vector_type(int ) vect_sorted);
int searchInSortedDatabase(int amount_of_books, int wanted_book_ID,  cvector_vector_type(int ) vect_sorted);
int searchBST(int amount_of_books, int wanted_book_ID, cvector_vector_type(int ) vect_sorted);



const char* PARAMETERS_FILE_NAME = "Parameters.txt";
const char* DATABASE_FILE_NAME = "AvailableBooks.txt";


int MAX_BUFFER_SIZE = 256;

int main(){
    time_t t;
    srand((unsigned) time(&t));

    //READING FROM PARAMETERS FILE
    const int simulationTime;
    const int MAX_SEARCH_TIME;
    const int MAX_ACTIVE_CLIENTS;
    const int CLIENT_INFORMING_TIME;

    FILE *fp1 = fopen(PARAMETERS_FILE_NAME, "r");
    if( fp1 == NULL){
        printf("file can't be opened \n");
    }

    if(fscanf(fp1, "%d", &simulationTime) < 1 ||
        fscanf(fp1, "%d", &MAX_SEARCH_TIME) < 1 ||
        fscanf(fp1, "%d", &MAX_ACTIVE_CLIENTS) < 1 ||
        fscanf(fp1, "%d", &CLIENT_INFORMING_TIME) < 1
       ){
        printf("Error reading parameter file.\n");
    }

    //READING FROM BOOK DATA BASE
    cvector_vector_type(int) vect_sorted = NULL;//creating a vector
    cvector_vector_type(int) vect_unsorted = NULL; //creating a vector

    int amount_of_books = 0;

    FILE *fp2 = fopen(DATABASE_FILE_NAME,"r");
    if( fp2 == NULL){
        printf("file can't be opened \n");
    }

    int integer; //element for reading
    while (fscanf(fp2, "%d", &integer) > 0) {
        cvector_push_back(vect_sorted,integer);
        cvector_push_back(vect_unsorted,integer);
        amount_of_books++;
    }

    //VARIABLES
    struct client activeClientsUnsorted[MAX_ACTIVE_CLIENTS];
    int noOfActiveClientsUnsorted = 0;
    int noOfWorkersUnsorted = 0;

    struct client activeClientsSorted[MAX_ACTIVE_CLIENTS];
    int noOfActiveClientsSorted = 0;
    int noOfWorkersSorted = 0;

    struct client activeClientsBST[MAX_ACTIVE_CLIENTS];
    int noOfActiveClientsBST = 0;
    int noOfWorkersBST = 0;

    int book_index = 0;


    //STARTING SIMULATION
    for(int current_time=0; current_time < simulationTime; current_time++){
        //Getting new client
        int newClientProbability = rand()%100;
        if( rand()%100 <= newClientProbability) //if new client exists
        {

            //CHECK IF BOOK EXISTS
            int newBookProbability = rand()%40+60; //chance of book existing 60 - 100%
            if( rand()%100 <= newBookProbability)   {

            //Generated wanted book name
            int wanted_book_ID = ReturnWantedBookID(amount_of_books, vect_sorted);

            //UNSORTED SEARCH
            activeClientsUnsorted[noOfActiveClientsUnsorted].time = current_time + searchInUnsortedDatabase(amount_of_books, wanted_book_ID, vect_unsorted); //add searching time
            activeClientsUnsorted[noOfActiveClientsUnsorted].bookID = wanted_book_ID;
            noOfActiveClientsUnsorted++;
            noOfWorkersUnsorted++;

            //SORTING ARRAY
            Selection_Sort(vect_sorted,amount_of_books);

            //SORTED SEARCH
            activeClientsSorted[noOfActiveClientsSorted].time = current_time + searchInSortedDatabase(amount_of_books, wanted_book_ID, vect_sorted);
            activeClientsSorted[noOfActiveClientsSorted].bookID = wanted_book_ID;
            noOfActiveClientsSorted++;
            noOfWorkersSorted++;

            //BST SEARCH
            activeClientsBST[noOfActiveClientsBST].time = current_time + searchBST(amount_of_books,wanted_book_ID,vect_sorted);
            activeClientsBST[noOfActiveClientsBST].bookID = wanted_book_ID;
            noOfActiveClientsBST++;
            noOfWorkersBST++;

            //REMOVE TAKEN BOOK FROM LISTS
            //UNSORTED
            book_index = ReturnBookIndex(vect_unsorted,amount_of_books, wanted_book_ID);
            cvector_erase(vect_unsorted, book_index);
            //SORTED
            book_index = ReturnBookIndex(vect_sorted,amount_of_books, wanted_book_ID);
            cvector_erase(vect_sorted, book_index);
            }
        }


        //REFRESH NUMBER OF CLIENTS FOR UNSORTED
            for(int i = 0; i< noOfActiveClientsUnsorted; i++){
                if(activeClientsUnsorted[i].time < current_time) //if service is completed
                {
                    //RETURING THE BOOK THAT WAS TAKEN
                    cvector_push_back(vect_unsorted, activeClientsUnsorted[i].bookID);
                    cvector_push_back(vect_sorted, activeClientsUnsorted[i].bookID);

                    //DELETING ELEMENTS fROM THE ARRAY
                    for(int j = i-1; j < noOfActiveClientsUnsorted-1; j++){
                        activeClientsUnsorted[j] = activeClientsUnsorted[j+1];
                    }
                    noOfWorkersUnsorted--;
                    noOfActiveClientsUnsorted--;
                }
            }

            //REFRESH NUMBER OF CLIENTS SORTED
            for(int i = 0; i< noOfActiveClientsSorted; i++){
                if(activeClientsSorted[i].time < current_time)
                {
                    //RETURING THE BOOK THAT WAS TAKEN
                    cvector_push_back(vect_unsorted, activeClientsSorted[i].bookID);
                    cvector_push_back(vect_sorted, activeClientsSorted[i].bookID);

                    //DELETING ELEMENTS fROM THE ARRAY
                    for(int j = i -1; j < noOfActiveClientsSorted -1; j++){
                        activeClientsSorted[j] = activeClientsSorted[j+1];
                    }
                    noOfWorkersSorted--;
                    noOfActiveClientsSorted--;
                }
            }

            //REFRESH NUMBER OF CLIENTS BST
            for(int i = 0; i< noOfActiveClientsBST; i++){
                if(activeClientsBST[i].time < current_time)
                {
                    //RETURING THE BOOK THAT WAS TAKEN
                    cvector_push_back(vect_unsorted, activeClientsBST[i].bookID);
                    cvector_push_back(vect_sorted, activeClientsBST[i].bookID);

                    //DELETING ELEMENTS fROM THE ARRAY
                    for(int j = i -1; j < noOfActiveClientsBST -1; j++){
                        activeClientsBST[j] = activeClientsBST[j+1];
                    }
                    noOfWorkersBST--;
                    noOfActiveClientsBST--;
                }

            }
    }
    //PROGRAM CLOSE
    printf("Workers Unsorted: %d\t Sorted: %d\tBST: %d\n", noOfWorkersUnsorted, noOfWorkersSorted, noOfWorkersBST);
    fclose(fp1);
    fclose(fp2);
}


int ReturnBookIndex(cvector_vector_type(int )vect, int amountOfBooks, int wantedBookID){
    int index = -1;
    for(int i = 0; i < amountOfBooks; i++){
        if(vect[i] == wantedBookID){
            index = i;
        }
    }
    return index;
}

int ReturnWantedBookID(int amountOfBooks, cvector_vector_type(int )vect_sorted){
    int index = abs(rand()%amountOfBooks);
    return vect_sorted[index];
}

int searchInUnsortedDatabase(int amount_of_books, int wanted_book_ID, cvector_vector_type(int ) vect_sorted){
    int search_time = 0;
    for(int i=0; i < amount_of_books; i++){
        if(vect_sorted[i] == wanted_book_ID) {
            search_time = i;
            break;
        }
    }
    return search_time;
}

void Selection_Sort(cvector_vector_type(int) vect, int n)
{
    int i, j, min_idx;
    // One by one move boundary of unsorted subarray
    for (int i = 0; i < n-1; i++)
    {
        // Find the minimum element in unsorted array
        min_idx = i;
        for (int j = i+1; j < n; j++){
            if (vect[j] < vect[min_idx]){
                min_idx = j;
            }
        }
        swap(&vect[min_idx], &vect[i]);
    }
}

void swap(int* xp, int* yp)
{
    int temp = *(xp);
    *(xp) = *(yp);
    *(yp) = temp;
}

int searchInSortedDatabase(int amount_of_books, int wanted_book_ID,  cvector_vector_type(int ) vect_sorted){
    int search_time = 0;
    for(int i = 0; i < amount_of_books; i++){
        if(vect_sorted[i] == wanted_book_ID){
            search_time = i;
            break;
        }
    }
    return search_time;
}

struct Node* sortedVectorToBST(cvector_vector_type(int ) vect_sorted, int amount_of_books){
    struct Node* root = NULL;
    for(int i = 0; i < amount_of_books; i++){
        int ID = vect_sorted[i];
        root = insert(root, ID);
    }
    return root;
}

int searchBST(int amount_of_books, int wanted_book_ID, cvector_vector_type(int ) vect_sorted){
    struct Node* root = sortedVectorToBST(vect_sorted,amount_of_books);
    return findDepth(root,wanted_book_ID);
}


