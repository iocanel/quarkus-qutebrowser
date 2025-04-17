{ pkgs ? import <nixpkgs> {} }:

pkgs.stdenv.mkDerivation {
  pname = "quteb";
  version = "1.0.0";

  src = ./.;

  nativeBuildInputs = [ pkgs.makeWrapper ];
  buildInputs = [ pkgs.zlib ];

  buildPhase = "true";

  installPhase = ''
    mkdir -p $out/bin
    cp target/quarkus-qutebrowser-1.0.0-SNAPSHOT-runner $out/bin/quteb
    chmod +x $out/bin/quteb

    wrapProgram $out/bin/quteb \
      --prefix LD_LIBRARY_PATH : ${pkgs.zlib}/lib
  '';
}
