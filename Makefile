# Makefile for audiobookman

NAME := quteb

native:
	./mvnw clean install -Pnative

nix-install: native
	nix-env -i -f native.nix
