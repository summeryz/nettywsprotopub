#!/usr/bin/env bash
#https://github.com/protocolbuffers/protobuf/releases
#https://github.com/protocolbuffers/protobuf/releases/tag/v26.1

../lib/protobuf/26_1/bin/protoc RedIMServerSideTransport.proto --java_out=../../../../../../../
../lib/protobuf/26_1/bin/protoc RedIMClientSideTransport.proto --java_out=../../../../../../../
../lib/protobuf/26_1/bin/protoc RedIMMessage.proto --java_out=../../../../../../../


#if (npm list -g | grep protobufjs-cli); then
#    echo "protobufjs-cli installed"
#else
#    echo "protobufjs-cli not installed"
#    npm install -g protobufjs-cli@1.1.2 -f
#fi
#
#
#ROOT_DIR=$(git rev-parse --show-toplevel)
#
#SRC_DIR="${ROOT_DIR}/src/main/java/com/xhs/redim/common/transport/protobuf/proto"
#
#OUT_DIR="${ROOT_DIR}/src/main/java/com/xhs/redim/common/transport/protobuf/js"
#
#rm -rf "${OUT_DIR}"
#mkdir -p "${OUT_DIR}"
#
#for PROTO_FILE in "${SRC_DIR}"/*.proto; do
#    BASE_NAME=$(basename "${PROTO_FILE}" .proto)
#
#    printf "Generating %s.js and %s.d.ts\n" "${BASE_NAME}" "${BASE_NAME}"
#
#    npx pbjs -t static-module -w es6 -o "${OUT_DIR}/${BASE_NAME}.js" "${PROTO_FILE}"
#
#    npx pbts -o "${OUT_DIR}/${BASE_NAME}.d.ts" "${OUT_DIR}/${BASE_NAME}.js"
#
#     # 在 .d.ts 文件的顶部添加一行注释
#    sed -i '' '1i\
#    /* eslint-disable @typescript-eslint/no-unused-vars */
#    ' "${OUT_DIR}/${BASE_NAME}.d.ts"
#
#done
#
