#!/bin/bash
set -x

ALIGN="java -cp OmegaT/OmegaT.jar:classes"

rm -rf tmx/segmented tmx/non-segmented
mkdir -p tmx/segmented tmx/non-segmented

align() {
  NAME=$1
  shift
  $ALIGN $@ true  tmx/segmented/$NAME.tmx || exit 1
  $ALIGN $@ false tmx/non-segmented/$NAME.tmx || exit 1
}

## Mozilla
align mozilla AlignMozilla data/mozilla/mozilla-central-* data/mozilla/be-*

## Ubuntu
align ubuntu AlignPO UTF-8 data/ubuntu/

## LibreOffice
align libreoffice AlignPO UTF-8 data/libreoffice/

## KDE3
align kde3 AlignPO UTF-8 data/kde3/

## KDE4
align kde4 AlignPO UTF-8 data/kde4/

## BSPlayer
align bsplayer AlignINI Cp1251 data-manual/bsplayer/English.lng data-manual/bsplayer/Belarusian.lng

## Opera
align opera AlignINI UTF-8 data-manual/opera/en.lng data-manual/opera/be.lng

## Wordpress
align wordpress AlignPO UTF-8 data-manual/wordpress/be_BY.po

## Drupal
align drupal-7.x AlignPO UTF-8 data/drupal-7.x/

## Gnome
align gnome-external-deps AlignPO UTF-8 data/gnome/external-deps/
align gnome-2-32 AlignPO UTF-8 data/gnome/gnome-2-32/
align gnome-3-6 AlignPO UTF-8 data/gnome/gnome-3-6/
align gnome-extras AlignPO UTF-8 data/gnome/gnome-extras/
align gnome-extras-stable AlignPO UTF-8 data/gnome/gnome-extras-stable/
align gnome-gimp AlignPO UTF-8 data/gnome/gnome-gimp/
align gnome-infrastructure AlignPO UTF-8 data/gnome/gnome-infrastructure/
align gnome-office AlignPO UTF-8 data/gnome/gnome-office/
align gnome-olpc AlignPO UTF-8 data/gnome/olpc/

for i in tmx/segmented/*.tmx tmx/non-segmented/*.tmx; do
  gzip -9 $i
done
