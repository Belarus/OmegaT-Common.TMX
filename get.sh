#!/bin/bash
set -x

ALIGN="java -cp ../OmegaT/OmegaT.jar:../classes"

cd data/

############################
nn() {
  echo ================= $1 =================
}
svnget() {
  mkdir -p $1 || exit 1
  pushd $1
  svn co $2 || exit 1
  popd
}
httpget() {
  mkdir -p $1 || exit 1
  pushd $1
  shift
  wget --no-check-certificate $@ || exit 1
  popd
}
############################

nn KDE
svnget kde3 svn://anonsvn.kde.org/home/kde/trunk/l10n-kde3/be/
svnget kde4 svn://anonsvn.kde.org/home/kde/trunk/l10n-kde4/be/

nn LibreOffice
httpget libreoffice https://translations.documentfoundation.org/export/POOTLE_EXPORT/libo_ui/be/libo_ui-be.zip
unzip -q libreoffice/libo_ui-be.zip -d libreoffice || exit 1

nn Ubuntu
#  See page https://translations.launchpad.net/ubuntu/<version(quantal)>/+language-packs, or https://translations.launchpad.net -> Choose version -> See all language packs -> Active language packs/Base pack
httpget ubuntu http://launchpadlibrarian.net/119214872/ubuntu-quantal-translations.tar.gz
pushd ubuntu; FILES=`tar tf *.tar.gz | grep /be/ | grep -v '/$'`; tar xfz *.tar.gz $FILES || exit 1; popd

nn Mozilla
httpget mozilla https://hg.mozilla.org/l10n-central/be/archive/tip.tar.bz2 -O be.tar.bz2
httpget mozilla https://hg.mozilla.org/mozilla-central/archive/tip.tar.bz2 -O en.tar.bz2
pushd mozilla; tar xfj be.tar.bz2 || exit 1; FILES=`tar tf en.tar.bz2 | grep /en-US/ | grep -v '/$'`; tar xfj en.tar.bz2 $FILES || exit 1; popd

nn Gnome
# page http://l10n.gnome.org/teams/be/ -> subpages, download all .po
gnome() {
  httpget gnome http://l10n.gnome.org/languages/be/$1/ui.tar.gz -O $1.tar.gz
  mkdir gnome/$1
  pushd gnome/$1
  tar xfz ../$1.tar.gz || exit 1
  popd
}
gnome gnome-3-6
gnome gnome-2-32
gnome external-deps
gnome gnome-office
gnome gnome-infrastructure
gnome gnome-gimp
gnome gnome-extras-stable
gnome gnome-extras
gnome olpc
