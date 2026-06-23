#!/bin/bash



OUTPUT_FILENAME="invariants.cats"
INVARIANT_FILENAME="invariant"

INVARIANT=$1

echo "The invariant is: ($INVARIANT)"
if [ "$#" -eq 0 ]
then
  echo "ERROR: No target procedure was given"
  return
fi

echo "Creating file $OUTPUT_FILENAME (or resetting if existing)"
echo "" > $OUTPUT_FILENAME
echo "Generating invariant ($INVARIANT) as CATs in $OUTPUT_FILENAME for "
OBS_TR_FML="x::OBS_VAR.\`${INVARIANT//"x"/"OBS_VAR"}\`"

PRECOND="${OBS_TR_FML//"OBS_VAR"/"oldX"}"
POSTCOND="${OBS_TR_FML//"OBS_VAR"/"newX"}"

for var in "${@:2}"
do
    CATNAME=$var"CAT"
    PROC_TARGET="$var"
    echo "- \"$var\" as \"$CATNAME\""
    echo "[$CATNAME] $PROC_TARGET :" | tee -a $OUTPUT_FILENAME
    echo " assumes: ~~ ** $PRECOND;" | tee -a $OUTPUT_FILENAME
    echo " ensures: ~~ ** $POSTCOND;" | tee -a $OUTPUT_FILENAME
    echo " expects: ~~;" | tee -a $OUTPUT_FILENAME
done

