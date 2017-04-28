git checkout master
sbt generateExamples
git checkout gh-pages
cp -rf examples/target/html examples0
rm -rf examples
cp -rf examples0 examples
rm -rf examples0
git commit -a -m "Update documentation"
git push origin gh-pages
