package org.scalafmt.config

import metaconfig._

/** ADT representing import selectors settings, specifically pertaining to the
  * handling when multiple names are imported from the same package.
  *
  * When [[org.scalafmt.config.ImportSelectors.noBinPack]] is selected, imports
  * are organized such that each line contains a single name imported from the
  * base package:
  * {{{
  *   // max columns     |
  *   import org.{
  *     Aaaa,
  *     Bbbb,
  *     C,
  *     D,
  *     Eeee
  *   }
  * }}}
  *
  * When [[org.scalafmt.config.ImportSelectors.binPack]] is selected, imports
  * are organized such that each line contains as many names as will fit within
  * the column limit:
  * {{{
  *   // max columns     |
  *   import org.{
  *     Aaaa, Bbbb, C, D,
  *     Eeee
  *   }
  * }}}
  *
  * When [[org.scalafmt.config.ImportSelectors.singleLine]] is selected,
  * imports are organized such that all names for a single package are arranged
  * on a single line:
  * {{{
  *   // max columns     |
  *   import org.{Aaaa, Bbbb, C, D, Eeee}
  * }}}
  */
sealed abstract class ImportSelectors extends Decodable[ImportSelectors] {
  override protected[config] def baseDecoder = ImportSelectors.reader
}

object ImportSelectors {

  val reader: ConfCodec[ImportSelectors] =
    ReaderUtil.oneOf[ImportSelectors](noBinPack, binPack, singleLine)

  implicit val encoder: ConfEncoder[ImportSelectors] = reader

  // This reader is backwards compatible with the old import selector
  // configuration, which used the boolean flag binPackImportSelectors to
  // decide between (what are now) the `binPack` and `noBinPack` strategies.
  // It is defined here to keep the `ConfDecoder.instance[T} {}` lambda in a
  // separate file from the @DeriveConfDecoder macro annotation; this is due to
  // limitations in the current version of scalameta/paradise, but these will
  // likely be fixed in the future, at which point this reader could be moved
  // to ScalafmtConfig
  implicit val preset: PartialFunction[Conf, ImportSelectors] = {
    case Conf.Bool(true) => ImportSelectors.binPack
    case Conf.Bool(false) => ImportSelectors.noBinPack
  }

  case object noBinPack extends ImportSelectors
  case object binPack extends ImportSelectors
  case object singleLine extends ImportSelectors

}
