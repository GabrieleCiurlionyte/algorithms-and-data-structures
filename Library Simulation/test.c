#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Binary_search_tree.h"


int main(){

int main()
{
struct Node *root = NULL;
root = insert(root, "a");
root = insert(root, "b");
root = insert(root, "c");
root = insert(root, "d");
root = insert(root, "e");
root = insert(root, "f");
root = insert(root, "h");
root = insert(root, "g");
root = insert(root, "k");

printf("Preorder traversal of the constructed AVL"
		" tree is \n");
preOrder(root);


printf("%d", findDepth(root,"k")+1);
return 0;
}
}


