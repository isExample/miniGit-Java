#!/bin/bash

# 상위 디렉터리로 이동
cd "$(dirname "$0")/.." || exit 1

# 프로젝트 루트 경로 설정
TEST_DIR="$(pwd)/test_workspace"

# 기존 디렉터리 삭제
#rm -rf "$TEST_DIR"

# 테스트 디렉터리 생성
mkdir -p "$TEST_DIR"/dir1/dir1-1
mkdir -p "$TEST_DIR"/dir2/dir2-1
mkdir -p "$TEST_DIR"/dir2/dir2-2/dir2-2-1

# 테스트 파일 생성 및 내용 추가
echo "Hello file1!" > "$TEST_DIR"/dir1/file1.txt
echo "Hello file2!" > "$TEST_DIR"/dir1/file2.txt
echo "Hello file3!" > "$TEST_DIR"/dir1/file3.txt
echo "Hello file4!" > "$TEST_DIR"/dir1/dir1-1/file4.txt

echo "Hello file5!" > "$TEST_DIR"/dir2/file5.txt
echo "Hello file6!" > "$TEST_DIR"/dir2/dir2-1/file6.txt
echo "Hello file7!" > "$TEST_DIR"/dir2/dir2-2/dir2-2-1/file7.txt

echo "Hello test!" > "$TEST_DIR"/test.txt

echo "Test environment set up successfully!"
