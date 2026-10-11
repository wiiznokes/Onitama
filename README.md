# Onitama

## Rules

But : capturer le maître adverse ou déplacer son propre maître sur le temple ennemi.
À chaque tour : jouer une carte de mouvement pour déplacer un de ses pions selon le schéma indiqué.


https://github.com/user-attachments/assets/c6f5be0b-a1c0-440b-92e1-8019c206f8f7

## How to run

make run

## Fonctionnement de l'IA

L'IA utilise l'algorithme **Minimax avec élagage alpha-bêta** pour explorer les coups possibles sur plusieurs tours et choisir le meilleur mouvement selon la profondeur de recherche définie par la difficulté.

Chaque état du jeu est évalué à l'aide de quatre critères pondérés : **nombre de pièces**, **sécurité des rois**, **distance du roi au trône adverse** et **proximité du centre du plateau**.

L'IA maximise son score tout en anticipant les meilleurs coups de l'adversaire, et utilise l'élagage alpha-bêta pour éviter d'explorer les branches inutiles et accélérer la recherche.

## 

Wiki

https://en.wikipedia.org/wiki/Onitama
