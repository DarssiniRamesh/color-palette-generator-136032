#!/bin/bash
cd /home/kavia/workspace/code-generation/color-palette-generator-136032/color_palette_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

